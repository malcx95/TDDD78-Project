package se.liu.ida.malvi108.tddd78.project.gui.dialogs;

enum RingToneOption
{
    NONE,
    BLIP,
    PLING,
    WHISTLE,
    TIME_HAS_COME,
    CUSTOM;

    @Override public String toString() {
	switch (this){
	    case NONE:
		return "Ingen";
	    case BLIP:
		return "Armbandsur";
	    case PLING:
		return "Glas";
	    case WHISTLE:
		return "Vissling";
	    case TIME_HAS_COME:
		return "\"Tiden är kommen\"";
	    case CUSTOM:
		return "Anpassad...";
	    default:
		return "";
	}
    }
}
