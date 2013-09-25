package org.bimserver;

import org.bimserver.models.ifc2x3tc1.Ifc2x3tc1Package;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;

public class Test1 {
	public static void main(String[] args) {
		for (EClassifier eClassifier : Ifc2x3tc1Package.eINSTANCE.getEClassifiers()) {
			if (eClassifier instanceof EClass) {
				if (Ifc2x3tc1Package.eINSTANCE.getIfcProduct().isSuperTypeOf((EClass) eClassifier)) {
					System.out.println(eClassifier.getName());
				}
			}
		}
	}
}
