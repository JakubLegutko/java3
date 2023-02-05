import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.CyclicBarrier;

public class PMO_TestService extends UnicastRemoteObject
		implements PMO_SimpleCallInterface, RegistrableRemoteInterface, Runnable {

	protected PMO_TestService() throws RemoteException {
		super();
	}

	private static final long serialVersionUID = 7831055381870829906L;
	private CyclicBarrier barrier;
	private long secretCode;
	private String preferredServiceName;
	private String serviceName;
	private RegistrationHelperInterface registrationHelper;
	private boolean stateOK = true;

	public void setCyclicBarrier(CyclicBarrier barrier) {
		this.barrier = barrier;
	}

	public void setPreferredServiceName(String preferredServiceName) {
		this.preferredServiceName = preferredServiceName;
	}

	public void setRegistrationHelperInterface(RegistrationHelperInterface registrationHelper) {
		this.registrationHelper = registrationHelper;
	}

	public long getSecretCode() {
		return secretCode;
	}

	public boolean test() {
		return stateOK;
	}

	@Override
	public String getPreferredServiceName() {
		PMO_Log.log("Wykonano metode getPreferredServiceName; zwrocila " + preferredServiceName);
		return preferredServiceName;
	}

	public String getServiceName() {
		return serviceName;
	}

	@Override
	public void secretCode(long value) throws RemoteException {
		PMO_Log.log("Odebrano secretCode = " + value);
		this.secretCode = value;
	}

	@Override
	public void run() {
		PMO_Log.log("Rejestracja serwisu " + preferredServiceName + " przed bariera");
		PMO_ThreadsHelper.wait(barrier);
		PMO_Log.log("Rejestracja serwisu " + preferredServiceName + " za bariera");

		try {
			PMO_Log.log("Rejestracja serwisu " + preferredServiceName);
			serviceName = registrationHelper.register(this);
			PMO_Log.log("Rejestracja serwisu " + preferredServiceName + " zarejestrowany pod nazwa " + serviceName);
			PMO_SystemOutRedirect.println(
					"Rejestracja serwisu " + preferredServiceName + " zarejestrowany pod nazwa " + serviceName);
			stateOK = true;
		} catch (Exception e) {
			PMO_CommonErrorLog.error("W trakcie rejestracji doszlo do wyjatku" + e.getMessage());
			stateOK = false;
		}

	}

}
