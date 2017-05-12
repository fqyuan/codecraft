package com.fqy.ga;

/*
 * Manage a population of candidate servers
 */
public class Population {
	// Hold population of servers
	Location[] loc;

	// Construct a population
	public Population(int populationSize, boolean iniFlag) {
		loc = new Location[populationSize];

		// If we need to initialise a population of tours do so
		if (iniFlag) {
			// Loop and create individuals
			for (int i = 0; i < loc.length; i++) {
				Location newLocation = new Location();
				newLocation.generateIndividual();
				saveLocation(i, newLocation);
			}
		}
	}

	public void saveLocation(int i, Location newLocation) {
		// TODO Auto-generated method stub

	}

}