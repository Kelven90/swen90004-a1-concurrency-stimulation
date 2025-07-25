/**
 * Student Name: Kelven Lai
 * Student ID: 1255199
 */

/**
 * Create a Monitor class (Treatment) for severly injured patients
 * to be treated by specialist, and transfer the treated patients back to Foyer.
 */

public class Treatment {

    // A flag indicating where there's patient allocated to the Treatment room
    private volatile boolean patientAllocated = false;

    // Variable that stores the information of the allocated patient in Treatment
    private volatile Patient currentPatient;

    // A flag indicating where there's patient allocated is done with treatment
    private volatile boolean treatmentDone = false;

    /**
     * Accepts patients from Triage into the Treatment room if there's an available slot.
     */
    public synchronized void acceptPatientTreatment(Triage triage, Nurse nurse, Orderlies orderlies)
    {
        // Transfer time from Triage to Treatment room
        try {
            Thread.sleep(Params.TRANSFER_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        // Wait outside if treatment room is currently occupied
        while (currentPatient != null && patientAllocated && nurse.orderliesAllocated) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Let the patient enters the treatment room if the patient is
        // severely injured and undergone triage examination
        if (triage.isPatientNeedTreatment() && nurse.patientAllocated != null 
            && nurse.orderliesAllocated && !nurse.patientAllocated.treated) {
            currentPatient = nurse.patientAllocated;
            patientAllocated = true;
            System.out.println(nurse.patientAllocated + " enters treatment room.");

            if (orderlies != null) {
                orderlies.releaseOrderlies(nurse);
            }
            notifyAll();
        }
    }

    /**
     * Specialist enters the treatment room to treat patient.
     */
    public synchronized void specialistEntersTreatment(Specialist specialist) {

        // The time a specialist spends elsewhere between treating patients
        try {
            wait(Params.SPECIALIST_AWAY_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (!specialist.inTreatmentRoom && !specialist.isTreatingPatient) {
            specialist.inTreatmentRoom = true;
            System.out.println("Specialist enters treatment room.");
            notifyAll();
        }
    }

    /**
     * Specialist treats patient that is in the treatment room.
     */
    public synchronized void treatingPatient(Specialist specialist) {

        // Wait if there's currently no available patient in the room
        while (currentPatient == null && !patientAllocated) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // The specialist started treating the severely injured patient in a given time
        // and leaves after the treatment is done
        if (!currentPatient.treated && specialist.inTreatmentRoom && !specialist.isTreatingPatient) 
        {
            System.out.println(currentPatient + " treatment started.");
            
            try {
                wait(Params.TREATMENT_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println(currentPatient + " treatment complete.");
            currentPatient.treated = true;
            treatmentDone = true;
            specialist.isTreatingPatient = false;
            specialistLeavesTreatment(specialist);
            notifyAll();
        }

    }

    /**
     * Specialist leaves the treatment room after the patient is treated.
     */
    public synchronized void specialistLeavesTreatment(Specialist specialist) {

        // the time a specialist leaves after treating patient
        try {
            wait(Params.SPECIALIST_LEAVE_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        specialist.inTreatmentRoom = false;
        System.out.println("Specialist leaves treatment room.");

    }

    /**
     * Allowing the patient to leave Treatment room after being treated 
     * with the help from orderlies recruited by the nurse
     */
    public synchronized void leaveTreatment(Nurse nurse, Orderlies orderlies) {

        if (currentPatient != null) {
            if (orderlies != null && currentPatient.treated && nurse.patientAllocated != null) {
                nurse.patientAllocated.treated = true;
                orderlies.recruitOrderlies(nurse);
            }
        }
        
        // Patient leaving Triage after nurse and orderlies have been allocated
        if (nurse.orderliesAllocated && currentPatient != null && patientAllocated) {
            System.out.println(currentPatient + " leaves treatment room.");
            currentPatient = null;
            patientAllocated = false;
            notifyAll();
        }

    }

    /**
     * Checks if the patient has done with the treatment process
     */
    public synchronized boolean getPatientTreatmentCondition() {
        return treatmentDone;
    }

}
