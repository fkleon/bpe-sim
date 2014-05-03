package co.nz.leonhardt.sim.model.entity;

import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

/**
 * A human resource.
 * 
 * @author freddy
 *
 */
public class Resource extends Entity {

	public Resource(Model owner, String name) {
		super(owner, name, true);
	}

}
