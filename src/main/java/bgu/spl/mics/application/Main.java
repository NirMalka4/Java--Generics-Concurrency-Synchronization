package bgu.spl.mics.application;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {

		Gson gson = new Gson();
		Input input;
		try {
			Reader reader = new FileReader(args[0]);
			input = gson.fromJson(reader, Input.class);
			Attack[] attacks = input.getAttacks();
			Integer R2D2dur = input.getR2D2();
			Integer Landodur = input.getLando();
			Integer numOfEwoks = input.getEwoks();

			Ewoks.getInstance().init(numOfEwoks);
			MicroService l1 = new LeiaMicroservice(attacks);
			Thread m1 = new Thread(l1); //Leia
			MicroService l2 = new HanSoloMicroservice();
			Thread m2 = new Thread(l2);//Han
			MicroService l3 = new C3POMicroservice();
			Thread m3 = new Thread(l3);//C3P0
			MicroService l4 = new R2D2Microservice(R2D2dur);
			Thread m4 = new Thread(l4); //R2D2
			MicroService l5 = new LandoMicroservice(Landodur);
			Thread m5 = new Thread(l5);//LANDO
			MessageBus bus = MessageBusImpl.getInstance();
			Diary diary = Diary.getInstance();
			m1.start();
			m2.start();
			m3.start();
			m4.start();
			m5.start();
			try {
				m1.join();
				m2.join();
				m3.join();
				m4.join();
				m5.join();
			} catch (InterruptedException ignored) {};

			Writer writer = new FileWriter(args[1]);
			Map<String,Object> map= new HashMap<>();

			map.put("totalAttacks",diary.getTotalAttacks());
			map.put("HanSoloFinish",diary.getHanSoloFinish());
			map.put("C3POFinish", diary.getC3POFinish());
			map.put("R2D2Deactivate", diary.getR2D2Deactivate());
			map.put("LeiaTerminate", diary.getLeiaTerminate());
			map.put("HanSoloTerminate", diary.getHanSoloTerminate());
			map.put("C3POTerminate",diary.getC3POTerminate());
			map.put("R2D2Terminate", diary.getR2D2Terminate());
			map.put("LandoTerminate",diary.getLandoTerminate());

			// convert map to JSON File
			new Gson().toJson(map, writer);

			// close the writer
			writer.close();
		}
		catch (FileNotFoundException e){ System.out.println("Input file not found"); }
		catch (IOException e) { System.out.println("Output file not found");}
	}
}


