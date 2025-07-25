/**
 * Student Name: Kelven Lai
 * Student ID: 1255199
 */

/**
 * Orderlies class is created for managing the shared resources (orderlies) that can
 * be used by the nurses.
 */
public class Orderlies {

    // Showing the number of orderlies that are available
    protected volatile int numOrderlies = Params.ORDERLIES;

    /**
     * Checking if there's sufficient orderlies left for transferring the patient.
     */
    public boolean sufficientOrderlies() {
        return (numOrderlies >= Params.TRANSFER_ORDERLIES);
    }

    /**
     * Nurse recruiting the required number of orderlies for transferring the patient.
     */
    public void recruitOrderlies(Nurse nurse) {

        if (!nurse.orderliesAllocated && nurse.allocated && sufficientOrderlies()) {
            numOrderlies -= Params.TRANSFER_ORDERLIES;
            System.out.println(nurse + " recruits " + Params.TRANSFER_ORDERLIES + 
            " orderlies (" + numOrderlies + " free).");
            nurse.orderliesAllocated = true;
        }
    }

    /**
     * Nurse releasing the recruited orderlies upon arrival of the destination.
     */
    public void releaseOrderlies(Nurse nurse) {
        
        if (nurse.orderliesAllocated) {
            numOrderlies += Params.TRANSFER_ORDERLIES;
            nurse.orderliesAllocated = false;
            System.out.println(nurse + " releases " + Params.TRANSFER_ORDERLIES + 
                " orderlies (" + numOrderlies + " free).");
        }
    }
    
}
