/**
 * Student Name: Kelven Lai
 * Student ID: 1255199
 */

/**
 * Create a Monitor class (Triage) for assessing patient's injury and determine 
 * their treatment pathway: If severe, transfer to Treatment room, else
 * transfer back to Foyer.
 */
public class Triage {

    // Variables that indicate whether Triage is allocated with patient
    private volatile boolean patientAllocated = false;

    // Variable that stores the information of the allocated patient to Triage
    private volatile Patient currentPatient;

    // Indicating the patient has undergone the triage process and whether they need treatment
    private volatile boolean needTreatment;

    /**
     * Accepting patients into Triage if there's available slot.
     */
    public synchronized void acceptPatientTriage(Nurse nurse, Orderlies orderlies) {

        // Transfer time from Foyer to Triage
        try {
                Thread.sleep(Params.TRANSFER_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        // Wait outside if triage is currently occupied
        while (currentPatient != null && patientAllocated) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Allow patient to enter triage
        if (nurse.patientAllocated != null && nurse.orderliesAllocated) {
            currentPatient = nurse.patientAllocated;
            patientAllocated = true;
            System.out.println(currentPatient + " enters triage.");
        }
        
        if (orderlies != null) {
            orderlies.releaseOrderlies(nurse);
        }

        triageExamination();
        notifyAll();
    }

    /**
     * Stimulates the scenerio where patient undergoes examination in Triage.
     */
    public synchronized void triageExamination() {

        // Time taken to examine a patient
        if (currentPatient != null) {
            try {
                wait(Params.TRIAGE_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (currentPatient != null) {
            needTreatment = currentPatient.Severe();
            notifyAll();
        }
    }

    /**
     * Allowing the patient to leave Triage with the help from orderlies recruited by the nurse.
     */
    public synchronized void leaveTriage(Nurse nurse, Orderlies orderlies) {

        if (orderlies != null && currentPatient != null) {
            orderlies.recruitOrderlies(nurse);
        }
        
        if (currentPatient != null) {
            if (nurse.orderliesAllocated) {
                System.out.println(currentPatient + " leaves triage.");
                currentPatient = null;
                patientAllocated = false;
                notifyAll();
            }
        }
    }
    
    /**
     * Checks if the patient in need of treatment.
     */
    public synchronized boolean isPatientNeedTreatment() {
        return needTreatment;
    }
}
