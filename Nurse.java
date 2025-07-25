/**
 * Student Name: Kelven Lai
 * Student ID: 1255199
 */

/**
 * Create a Nurse class that are responsible for transferring patients between locations
 * with the helps of orderlies after being allocated to a patient.
 */

public class Nurse extends Thread {

    // A flag indicating whether a nurse is allocated to a patient
    protected volatile boolean allocated = false;

    // A unique identifier for the nurse
    private int id;

    // Variable that stores the allocated patient of the nurse
    protected volatile Patient patientAllocated;

    // Reference variables for different locations in the ED
    private Foyer foyer;
    private Triage triage;
    private Treatment treatment;

    // Reference variables for the shared resources Orderlies
    protected volatile Orderlies orderlies;

    // A flag indicating whether orderlies is allocated to the nurse
    protected volatile boolean orderliesAllocated;

    // Create a new nurse with a given identifier and the locations of ED as parameters
    public Nurse(int id, Foyer foyer, Triage triage, Orderlies orderlies, Treatment treatment) {
        this.id = id;
        this.foyer = foyer;
        this.triage = triage;
        this.orderlies = orderlies;
        this.treatment = treatment;
    }

    public void run() {
        while (!isInterrupted()) {
            // Nurse workflow steps following the stimulation
            foyer.allocatPatientToNurse(this);
            foyer.leaveFoyer(this, orderlies);
            triage.acceptPatientTriage(this, orderlies);
            triage.leaveTriage(this, orderlies);
            treatment.acceptPatientTreatment(triage, this, orderlies);
            treatment.leaveTreatment(this, orderlies);
            foyer.acceptPatientFoyer(triage, treatment, this, orderlies);
            foyer.patientReleased(this);
        }
    }

    /**
     * Produce an identifying string for the patient
     */
    public String toString() {
    	String s = "Nurse " + id;
        return s;
    }
    
}
