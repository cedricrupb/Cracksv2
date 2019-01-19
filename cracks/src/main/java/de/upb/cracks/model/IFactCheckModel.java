package de.upb.cracks.model;

import de.upb.cracks.io.FactCheckQueryEntity;

public interface IFactCheckModel {

    public double eval(FactCheckQueryEntity queryEntity);

}
