package tests;

import java.io.File;
import java.io.IOException;

import training.session.FtFBPTrainingSession;

public class TrainingDataGenerationTest {
	public static void main(String[] args) {
		FtFBPTrainingSession ts = new FtFBPTrainingSession(new File("sdgsg/"), "fjfjfj", 0, 100);
		ts.train(0);
		try {
			ts.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
