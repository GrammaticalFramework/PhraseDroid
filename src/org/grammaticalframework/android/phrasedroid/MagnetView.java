/* MagnetView.java
 * 
 * this is a custom widget. It has the following properties :
 * - display an input field with a drop down list of magnets
 * - filter the list of magnets when a prefix is typed
 * - display controls for clearing the input and for removing the last magnet
 */

class MagnetView {

    // API
    void setMagnets(String[] magnets){}
    void setMagnets(String[] magnets, String[] predictions){}
    
    class MagnetListener {
	void onAddMagnet(){}
	void onRemoveMagnet(){}
	void onClearMagnets(){}
    }
	


}