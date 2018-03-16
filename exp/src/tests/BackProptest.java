package tests;

import java.io.File;

import training.session.FtFBPTrainingSession;

public class BackProptest {

	public static void main(String[] args) {
		FtFBPTrainingSession ts = new FtFBPTrainingSession(new File("sdgsg/"), "fjfjfj", 0, 1000);
		ts.train(1000000);
	}
}
