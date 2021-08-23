package bgu.spl.mics;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	//Utility container used for handling Microservices messages queue
	private final Map<MicroService, Queue<Message>> servicesQueueMap;

	// Utility container used for distributing Events messages to relevant addressees
	private final Map<Class<? extends Event>, Queue<MicroService>> subscribersMap;

	// Utility container used for distributing broadcast messages to relevant addressees
	private final Map<Class<? extends Broadcast>, Queue<MicroService>> broadcastMap;

	//Utility container used for matching Event to its corresponding Future
	//Event will be mapped according to its identity hash code
	private final Map<Integer, Future> resolvedEvents;

	//Locks used to ensure only one Queue/List will be created for some Message type.
	private final Object Create_Event_Lock;
	private final Object Create_Broadcast_Lock;


	private MessageBusImpl() {
		broadcastMap = new HashMap<>();
		subscribersMap = new HashMap<>();
		servicesQueueMap = new HashMap<>();
		resolvedEvents = new ConcurrentHashMap<>();
		Create_Event_Lock=new Object();
		Create_Broadcast_Lock= new Object();
	}

	//Establish thread-safe singleton
	private static class MessageBusImplHolder {
		private static final MessageBusImpl instance = new MessageBusImpl();
	}

	public static MessageBusImpl getInstance() {
		return MessageBusImplHolder.instance;
	}

	/**
	 * @PRE: Microservice m ought be already registered, thus exists at the servicesQueue Map
	 * @INV: Microservice register only once
	 */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (servicesQueueMap.containsKey(m)){
			if (subscribersMap.containsKey(type))
					subscribersMap.get(type).add(m);
			else {
				synchronized (Create_Event_Lock) {
					//Verify whether Event type has not been inserted to the subscribersMap
					//in order to prevent undesirable updates(deleting existing Queue associated with type key)
					if (!subscribersMap.containsKey(type)) {
						Queue<MicroService> q = new ConcurrentLinkedQueue<>();
						q.add(m);
						subscribersMap.put(type, q);
					}//if
					else
						subscribersMap.get(type).add(m);
				}//synchronized
			}//else
		}//if
	}

	/**
	 * @PRE: Microservice m ought be already registered, thus exists at the broadcastAddressee Map
	 * @INV: Microservice register only once
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (servicesQueueMap.containsKey(m)) {
			if (broadcastMap.containsKey(type))
				broadcastMap.get(type).add(m);
			else {
				synchronized (Create_Broadcast_Lock) {
					//Verify whether Broadcast type has not been inserted to the broadcastMap
					//in order to prevent undesirable updates (deleting the existing Queue associated with type key)
					if (!broadcastMap.containsKey(type)) {
						Queue<MicroService> q = new ConcurrentLinkedQueue<>();
						q.add(m);
						broadcastMap.put(type, q);
					}//if
					else
						broadcastMap.get(type).add(m);
				}//synchronized
			}//else
		}//if
	}


	@Override
	@SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		if (e != null && resolvedEvents.get(System.identityHashCode(e))!=null) {
			resolvedEvents.get(System.identityHashCode(e)).resolve(result);
		}
	}

	/**
	 * @PRE: Microservice m has registered to Broadcast b upon initializing
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		if (b!=null && broadcastMap.containsKey(b.getClass())) {
			//The list which associated with b.getClass() key contains all MicroServices registered to Broadcast b
			//Hence, message b will be added to the queue of each one of them
			synchronized (broadcastMap.get(b.getClass())) {
				for (MicroService m : broadcastMap.get(b.getClass())) {
					if (servicesQueueMap.get(m) != null) {
						servicesQueueMap.get(m).add(b);
						//Notify each subscribed Microservice that a message was added to it's queue
						synchronized (servicesQueueMap.get(m)){
							servicesQueueMap.get(m).notify();
						}
					}//if
				}//for
			}//synchronized
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> res = null;
		if (e != null && subscribersMap.get(e.getClass()) != null) {
			//Therefore exists subscriber for this Event type
			MicroService assignedService;
			synchronized (subscribersMap.get(e.getClass())) {
				//Assign Event e to the first nominee at the subscribers queue
				//add the assigned service to the queue tail to prevail round-roubin distribution.
				assignedService = subscribersMap.get(e.getClass()).poll();
				if (assignedService == null || servicesQueueMap.get(assignedService) == null) return res;
				subscribersMap.get(e.getClass()).add(assignedService);
			}//synchronized
			res = new Future<>();
			resolvedEvents.put(System.identityHashCode(e), res);
			//Notify assignedService that a message was added to it's queue
			synchronized (servicesQueueMap.get(assignedService)) {
				servicesQueueMap.get(assignedService).add(e);
				servicesQueueMap.get(assignedService).notify();
			}
		}//if
		return res;
	}

	@Override
	public void register(MicroService m) {
		if (!servicesQueueMap.containsKey(m)) {
			Queue<Message> q= new ConcurrentLinkedQueue<>();
			servicesQueueMap.put(m, q);
		}
	}

	@Override
	public void unregister(MicroService m) {
		if (servicesQueueMap.containsKey(m)) {
			for(Message message : servicesQueueMap.get(m)){
				if(Event.class.isAssignableFrom(message.getClass())){
					//Resolve each future associated with Event message with null value
					// meaning Event message has not been handled
					resolvedEvents.get(System.identityHashCode(message)).resolve(null);
				}
			}
			//Remove Microservice m message queue
			servicesQueueMap.remove(m);
			//Remove Microservice m from each collection which it's belong to
			subscribersMap.forEach((type, queue) -> {
				queue.remove(m);
			});
			broadcastMap.forEach((type, queue) -> {
				queue.remove(m);
			});
		}
	}//if


	/**
	 * @PRE: servicesMap is already contains Microservice m
	 */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!servicesQueueMap.containsKey(m))
			throw new IllegalStateException("Microservice m should have been registered yet");
		Queue<Message> q= servicesQueueMap.get(m);
		while(q!=null && q.isEmpty()) {
			//Microservice m will wait until being notified by other threads whom send either Event or Broadcast
			synchronized (servicesQueueMap.get(m)) {
				servicesQueueMap.get(m).wait();
			}
		}
		Message msg= q.poll();
		return msg;
	}

	//Utility method, used at Ewoks class during threads attempts to acquire resources
	public Queue<Message> getQueue(MicroService m){
		return servicesQueueMap.get(m);
	}
}

