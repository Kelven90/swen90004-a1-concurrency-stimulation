/**
 * Student Name: Kelven Lai
 * Student ID: 1255199
 */

/**
 * Create a Monitor class (Foyer) for admitting and discharging patients,
 * and accepts the patients from both Triage and Treatment room.
 */

public class Foyer {

    // A flag indicating if there's a new admitted patient in Foyer
    private volatile boolean newAdmittedPatient = false;

    // A flag indicating if there's a waiting for discharge patient in Foyer
    private volatile boolean waitDischargePatient = false;
    
    // Variable that stores the information of the admitted patient
    private volatile Patient admittedPatient;

    // Variable that stores the information of the waiting for discharge patient
    private volatile Patient DischargingPatient;

    // A flag indicating whether the patient has been released by the nurse
    private volatile boolean isReleased = false;

    /**
     * Accepting new patient into Foyer if there's available slot.
     */
    public synchronized void arriveAtED(Patient newPatient) {

        // Wait if there's already a patient admitted to Foyer
        while (newAdmittedPatient) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        admittedPatient = newPatient;
        newAdmittedPatient = true;
        System.out.println(admittedPatient + " admitted to ED.");
        notifyAll();
    }

    /**
     * Allocate a free nurse for the newly admitted patient.
     */
    public synchronized void allocatPatientToNurse(Nurse nurse) {

        // Wait if there's no available patient for the allocation
        while (!newAdmittedPatient) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // Allocate a free nurse to the patient if there isn't already an allocated nurse for them
        if (!admittedPatient.allocated && !nurse.allocated && nurse.patientAllocated == null) {
            nurse.patientAllocated = admittedPatient;
            nurse.allocated = true;
            admittedPatient.allocated = true;
            System.out.println(nurse.patientAllocated + " allocated to " + nurse + ".");
            notifyAll();
        }
    }

    /**
     * Allowing the patient to leave Foyer with the help from orderlies recruited by the nurse.
     */
    public synchronized void leaveFoyer(Nurse nurse, Orderlies orderlies) {

        if (admittedPatient != null) {
            if (orderlies != null && nurse.patientAllocated != null && nurse.allocated 
                && admittedPatient.allocated) {
                orderlies.recruitOrderlies(nurse);
            }
        }

        if (nurse.orderliesAllocated) {
            System.out.println(admittedPatient + " leaves Foyer.");
            newAdmittedPatient = false;
            admittedPatient = null;
            notifyAll();
        }
    }

    /**
     * Accepting patients from both Triage or Treatment room and 
     * set them as discharging patients
     */
    public synchronized void acceptPatientFoyer(Triage triage, Treatment treatment, Nurse nurse, 
                                                Orderlies orderlies) {

            // Transfer time needed between two locations
            try {
                Thread.sleep(Params.TRANSFER_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            while (waitDischargePatient && DischargingPatient != null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Set the patient to be waiting for discharge if the patient
            // returns from either triage examination (not severe) or treated by specialist
            if (nurse.patientAllocated != null && nurse.orderliesAllocated && 
                (!triage.isPatientNeedTreatment() || treatment.getPatientTreatmentCondition())) {
                DischargingPatient = nurse.patientAllocated;
                waitDischargePatient = true;
                System.out.println(DischargingPatient + " enters Foyer.");
            }

            // Release the recruited orderlies upon arrival of the destination
            if (orderlies != null) {
                orderlies.releaseOrderlies(nurse);
            }

            notifyAll();
    }

    /**
     * Remove the patient that is in the "waiting for discharge" slot
     */
    public synchronized void departFromED() {

        if (waitDischargePatient && DischargingPatient != null && isReleased) {
            System.out.println(DischargingPatient + " discharged from ED.");
            DischargingPatient = null;
            waitDischargePatient = false;
            notifyAll();
        }
    }

    /**
     * Releasing the allocated patient by the nurse before discharging the patient
     */
    public synchronized void patientReleased(Nurse nurse) {
        
        if (waitDischargePatient && DischargingPatient != null) {
            System.out.println(nurse + " releases " + nurse.patientAllocated);
            nurse.allocated = false;
            nurse.patientAllocated = null;
            isReleased = true;
            notifyAll();
        }
    }
    
}
