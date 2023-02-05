
public class PMO_Connector {
	public static RegistrationHelperInterface connect() {
		try {
			return (RegistrationHelperInterface) java.rmi.Naming.lookup(RegistrationHelperInterface.SERVICE_NAME);
		} catch (Exception e) {
			PMO_CommonErrorLog
					.error("W trakcie pracy metody connect doszlo do wyjatku - polaczenie z serwisem niemozliwe");
			PMO_CommonErrorLog.criticalMistake();
			return null;
		}
	}

	public static PMO_SimpleCallInterface connect(String name) {
		try {
			return (PMO_SimpleCallInterface) java.rmi.Naming.lookup(name);
		} catch (Exception e) {
			PMO_CommonErrorLog.error("W trakcie pracy metody connect do serwisu o nazwie " + name
					+ " doszlo do wyjatku - polaczenie z serwisem niemozliwe");
			PMO_CommonErrorLog.criticalMistake();
			return null;
		}
	}

}
