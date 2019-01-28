package de.upb.cracks.model;

import de.upb.cracks.io.FactCheckQueryEntity;

import java.io.IOException;
import java.nio.file.Path;

public interface IFactCheckModel {

    public double eval(FactCheckQueryEntity queryEntity);

    public void store(Path p) throws IOException;

}
