import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfejs uslugi posrednika w rejestracji serwisow RMI w rmiregistry.
 */
public interface RegistrationHelperInterface extends Remote {

	/**
	 * Nazwa pod jaka serwis pomocniczy powinien zostac zarejestrowany.
	 */
	public final static String SERVICE_NAME = "REGISTRATION_HELPER";

	/**
	 * Metoda rejestruje przekazany serwis pod unikalna nazwa. Jesli jesli to
	 * mozliwe uzywana jest nazwa zwracana przez metode getPreferredServiceName.
	 * Jesli taka nazwa serwisu juz jest w uzyciu nalezy wygenerowac na podstawie
	 * getPreferredServiceName nazwe unikalna poprzez dodanie do preferowanej nazwy
	 * numera.
	 * 
	 * @param service
	 *            przekazany do zarejestrowania serwis
	 * @return nazwa pod jaka serwis zostal zarejestrowany
	 * @throws RemoteException
	 *             wyjatek, ktory moze zostac zgloszony w przypadku zaistnienia
	 *             bledu.
	 */
	public String register(RegistrableRemoteInterface service) throws RemoteException;

	/**
	 * Metoda odrejestrowuje serwis zarejestrowany pod nazwa serviceName.
	 * 
	 * @param serviceName
	 *            nazwa serwisu, ktory ma zostac odrejestrowany
	 * @return true - serwis zostal odrejestrowany, false - serwis nie zostal
	 *         odrejestrowany, gdyz serwis o podanej nazwie nie istnieje.
	 * @throws RemoteException
	 *             wyjatek, ktory moze zostac zgloszony w przypadku zaistnienia
	 *             bledu.
	 */
	public boolean unregister(String serviceName) throws RemoteException;
}
