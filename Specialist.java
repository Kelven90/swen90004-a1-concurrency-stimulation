/**
 * Student Name: Kelven Lai
 * Student ID: 1255199
 */

/**
 * Create a Specialist class that are responsible for treating patients in Treatment room
 * and leaves the Treatment room in between treating each Patient.
 */

public class Specialist extends Thread {

    // Reference variable for Treatment
    private Treatment treatment;

    // A flag indicating whether the specialist is in the treatment room
    protected boolean inTreatmentRoom;

    // A flag indicating whether the specialist is treating the patient
    protected boolean isTreatingPatient;

    /**
     * Constructs a Specialist object with Treatment as parameter.
     */
    public Specialist(Treatment treatment) {
        this.treatment = treatment;
    }

    public void run() {
        while (!isInterrupted()) {
            // Specialist workflow steps
            treatment.specialistEntersTreatment(this);
            treatment.treatingPatient(this);
        }
    }
    
}
