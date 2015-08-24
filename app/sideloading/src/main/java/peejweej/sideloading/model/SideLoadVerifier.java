package peejweej.sideloading.model;

import java.io.Serializable;

/**
 * Created by Fechner on 8/24/15.
 */
public abstract class SideLoadVerifier implements Serializable{

    abstract public boolean fileIsValid(String file);
    abstract public boolean fileIsValid(byte[] file);
}
