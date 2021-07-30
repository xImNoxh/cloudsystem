package de.polocloud.signs.executes;

public class ExecuteService {

    private ServiceInspectExecute serviceInspectExecute;
    private ServiceUpdateExecute serviceUpdateExecute;

    public ExecuteService() {
        this.serviceInspectExecute = new ServiceInspectExecute();
        this.serviceUpdateExecute = new ServiceUpdateExecute();
    }

    public ServiceUpdateExecute getServiceUpdateExecute() {
        return serviceUpdateExecute;
    }

    public ServiceInspectExecute getServiceInspectExecute() {
        return serviceInspectExecute;
    }

}
