import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;

public class PMO_SimpleTests implements PMO_RunTestTimeout {

	private final static int THREADS = 125;
	private final static String NAME = "SERVICE_";
	private boolean runDone = false;
	private List<PMO_TestService> services = new ArrayList<>();
	private RegistrationHelperInterface registrationHelper;

	@Override
	public void run() {

		registrationHelper = PMO_Connector.connect();
		CyclicBarrier cb = new CyclicBarrier(THREADS);

		PMO_TestService service;
		PMO_SimpleCallInterface simpleSerivice;
		Random rnd = new Random();
		long code;
		Set<String> names = new HashSet<>();
		try {
			for (int i = 0; i < THREADS; i++) {
				service = new PMO_TestService();
				service.setPreferredServiceName(NAME + (i % 10));
				service.setRegistrationHelperInterface(registrationHelper);
				service.setCyclicBarrier(cb);
				services.add(service);
				PMO_Log.log("Przygotowano serwis o nazwie " + service.getPreferredServiceName());
			}
			PMO_ThreadsHelper.joinThreads(PMO_ThreadsHelper.createAndStartThreads(services, true));

			for (PMO_TestService s : services) {
				PMO_Log.log("Service " + s.getPreferredServiceName() + " zarejestrowano jako " + s.getServiceName());
				if (!names.add(s.getServiceName())) {
					PMO_CommonErrorLog.error("Blad - wykryto powtorzenie nazwy serwisu");
					PMO_CommonErrorLog.criticalMistake();
				}
				simpleSerivice = PMO_Connector.connect(s.getServiceName());
				code = rnd.nextLong();
				simpleSerivice.secretCode(code);
				if (s.getSecretCode() != code) {
					PMO_CommonErrorLog.error("Blad wyslano kod do serwisu, a odebrano inny");
					return;
				} else {
					PMO_Log.log( "Odebrano poprawny kod");
					PMO_SystemOutRedirect.println( "Odebrano poprawny kod");
				}
			}

			runDone = true;
		} catch (RemoteException e) {
			PMO_CommonErrorLog.error("W trakcie pracy testu doszlo do wyjatku " + e.getMessage());
		}
	}

	@Override
	public boolean test() {
		if (!runDone) {
			PMO_CommonErrorLog.error("BLAD: test nie zostal zakonczony przed uruchomieniem testu");
			return false;
		}

		for (PMO_TestService service : services) {
			if (!service.test()) {
				PMO_CommonErrorLog.error("BLAD: wykryto blad w obsludze serwisu " + service.getPreferredServiceName());
				return false;
			}
		}
		return true;
	}

	@Override
	public long getRequiredTime() {
		return 15000;
	}

}
