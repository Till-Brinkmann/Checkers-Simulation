package network;
import java.io.Serializable;

import network.NetworkManager.InfoType;
public class Info implements Serializable{
	
	InfoType infoType;
	Object object;
	public Info(InfoType infoType, Object object) {
		this.infoType = infoType;
		this.object = object;
	}
	public Object getObject() {
		return object;
	}
	public InfoType getInfoType() {
		return infoType;
	}
}
