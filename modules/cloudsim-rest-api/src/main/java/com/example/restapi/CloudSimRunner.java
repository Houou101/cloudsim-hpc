package com.example.restapi;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class CloudSimRunner {

    public static double runCloudSimSimulation(String instanceName, int vcpus, double frequency, long cloudletLength) {
        try {
            // CloudSim initialisieren
            CloudSim.init(1, Calendar.getInstance(), false);

            // Erstelle das Rechenzentrum
            Datacenter datacenter = createDatacenter();

            // Erstelle den Broker
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            // Erstelle eine VM basierend auf der Instanzkonfiguration
            List<Vm> vmlist = new ArrayList<>();
            int vmId = 0;
            int mips = (int) (vcpus * frequency * 1000); // Umrechnung in MIPS
            long size = 10000; // Speicherplatz in MB
            int ram = 16000; // Arbeitsspeicher in MB
            long bw = 10000; // Bandbreite in MB
            int pesNumber = vcpus;
            String vmm = "Xen";

            Vm vm = new Vm(vmId, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            vmlist.add(vm);
            broker.submitGuestList(vmlist);

            // Erstelle einen Cloudlet (die zu simulierende Workload)
            List<Cloudlet> cloudletList = new ArrayList<>();
            int cloudletId = 0;
            long fileSize = 300;
            long outputSize = 300;
            UtilizationModel utilizationModel = new UtilizationModelFull();

            Cloudlet cloudlet = new Cloudlet(cloudletId, cloudletLength, pesNumber, fileSize, outputSize,
                    utilizationModel, utilizationModel, utilizationModel);
            cloudlet.setUserId(brokerId);
            cloudlet.setVmId(vmId);

            cloudletList.add(cloudlet);
            broker.submitCloudletList(cloudletList);

            // Simulation starten
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            // Ergebnisse abrufen
            List<Cloudlet> newList = broker.getCloudletReceivedList();

            // Prüfen, ob die Liste leer ist, bevor wir darauf zugreifen
                if (newList.isEmpty()) {
                    System.out.println("Fehler: Keine Cloudlet-Ergebnisse empfangen!");
                    return -1; // Oder eine andere Standard-Zeit setzen
                }

            return newList.get(0).getActualCPUTime();

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static Datacenter createDatacenter() {
        List<Host> hostList = new ArrayList<>();
        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(1000000))); // Max. MIPS der CPU

        int hostId = 0;
        int ram = 64000; // 64 GB RAM
        long storage = 1000000; // 1 TB Speicher
        long bw = 10000;

        hostList.add(new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw),
                storage, peList, new VmSchedulerTimeShared(peList)));

        String arch = "x86"; // Systemarchitektur
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 1.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.1;
        double costPerBw = 0.1;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter("Datacenter_0", characteristics, new VmAllocationPolicySimple(hostList),
                    new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datacenter;
    }

    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return broker;
    }
}
