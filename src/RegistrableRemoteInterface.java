import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistrableRemoteInterface extends Serializable, Remote {
	/**
	 * Metoda zwraca preferowana nazwe serwisu.
	 * 
	 * @return preferowana nazwa
	 */
	public String getPreferredServiceName() throws RemoteException;
}
