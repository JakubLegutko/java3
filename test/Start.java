import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Start implements RegistrationHelperInterface {

    Start() {// RMI init
        try {
            RegistrationHelperInterface stub = (RegistrationHelperInterface) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(RegistrationHelperInterface.SERVICE_NAME, stub);
        } catch ( RemoteException e ) {

            System.err.println("RMI-related reservation system exception:");
            e.printStackTrace();

        }
    }
    public synchronized String register(RegistrableRemoteInterface service) throws RemoteException{
        Registry registry = LocateRegistry.getRegistry();
        List<String> registeredNames =  new ArrayList<String>(Arrays.asList(registry.list()));
        Boolean complete = false;
        int i = 0;
        try {
            registry.bind(service.getPreferredServiceName(),service);
        } catch (AlreadyBoundException e) {
            while(true){
            i++;
                    try {
                        registry.bind(service.getPreferredServiceName()+i,service);
                        return service.getPreferredServiceName()+i;
                        
                    } catch (AlreadyBoundException d) {
                        // Will never happen, made sure of that.
                        e.printStackTrace();
                    }
            e.printStackTrace();
        }
        }
        return service.getPreferredServiceName();
    }
    public synchronized boolean unregister(String serviceName) throws RemoteException {

            Registry registry = LocateRegistry.getRegistry();
            List<String> registeredNames =  new ArrayList<String>(Arrays.asList(registry.list()));
            if (registeredNames.contains(serviceName)) {
                try {
                    registry.unbind(serviceName);
                } catch (NotBoundException e) {
                    // Will never happen but ok.
                    return false;
                }
                return true;
            }

        return false;
    }
}