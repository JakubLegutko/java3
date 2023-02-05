
public class PMO_Start {
	private static boolean runTest(PMO_RunTestTimeout test) {
		long timeToFinish = test.getRequiredTime();
		Thread th = new Thread(test);
		th.setDaemon(true);
		th.start();

		PMO_SystemOutRedirect.println("timeToFinish = " + timeToFinish);
		PMO_SystemOutRedirect.println("Maksymalny czas oczekiwania to " + (timeToFinish / 1000) + " sekund");

		long beforeJoin = java.lang.System.currentTimeMillis();
		try {
			th.join(timeToFinish);
		} catch (InterruptedException e) {
		}
		long remainingTime = timeToFinish - java.lang.System.currentTimeMillis() + beforeJoin;
		if (remainingTime > 0) {
			PMO_SystemOutRedirect.println("Dodatkowy czas: " + remainingTime + " msec");
			PMO_TimeHelper.sleep(remainingTime);
		}

		PMO_SystemOutRedirect.println("Zakonczyl sie czas oczekiwania na join()");

		if (th.isAlive()) {
			PMO_SystemOutRedirect.println("BLAD: Test nie zostal ukonczony na czas");
			PMO_ThreadWatcher.watch(th);
			return false;
		} else {
			PMO_SystemOutRedirect.println("Uruchamiam test");
			return test.test();
		}

	}

	private static void shutdownIfFail(boolean testOK) {
		if (!testOK) {
			PMO_Verdict.show(false);
			shutdown();
		}
	}

	private static void showTest(String txt) {
		PMO_Log.log("");
		PMO_Log.log("xxxxx  " + txt + "  xxxxx");
		PMO_Log.log("");
		PMO_SystemOutRedirect.println("+------------------------+");
		PMO_SystemOutRedirect.println("|                        |");
		PMO_SystemOutRedirect.println("+-- " + txt + " --+");
		PMO_SystemOutRedirect.println("|                        |");
		PMO_SystemOutRedirect.println("+------------------------+");
	}

	private static void shutdown() {
		java.lang.System.out.println("HALT");
		Runtime.getRuntime().halt(0);
		java.lang.System.out.println("EXIT");
		java.lang.System.exit(0);
	}

	public static void main(String[] args) {

		PMO_SystemOutRedirect.startRedirectionToNull();

		PMO_UncaughtException uncaughtExceptionsLog = new PMO_UncaughtException();
		boolean result = true;

		showTest("   TEST    ");
		result &= runTest(new PMO_SimpleTests());
		result &= PMO_CommonErrorLog.isStateOK();

		if (!result)
			PMO_Log.showLog();

		if (!result) {
			PMO_SystemOutRedirect.println("--- log bledow ---");
			PMO_CommonErrorLog.getErrorLog(0).forEach(PMO_SystemOutRedirect::println);
		}

		if (!uncaughtExceptionsLog.isEmpty()) {
			PMO_SystemOutRedirect.println("--- log wyjatkow ---");
			PMO_SystemOutRedirect.println(uncaughtExceptionsLog.toString());
		}

		PMO_Verdict.show(result);

		shutdown();
	}
}
