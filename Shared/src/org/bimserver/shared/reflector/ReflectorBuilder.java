package org.bimserver.shared.reflector;

/******************************************************************************
 * Copyright (C) 2009-2013  BIMserver.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/

import java.util.Collections;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.UserException;
import org.bimserver.shared.interfaces.NotificationInterface;
import org.bimserver.shared.interfaces.PublicInterface;
import org.bimserver.shared.interfaces.ServiceInterface;
import org.bimserver.shared.meta.SMethod;
import org.bimserver.shared.meta.SParameter;
import org.bimserver.shared.meta.SService;
import org.bimserver.shared.meta.ServicesMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectorBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectorBuilder.class);
	private ServicesMap servicesMap;
	private ClassPool pool;
	private static int implementationCounter = 0;
	private static final String GENERATED_CLASSES_PACKAGE = "org.bimserver.generated";

	public ReflectorBuilder(ServicesMap servicesMap) {
		this.servicesMap = servicesMap;
	}

	public static void main(String[] args) {
		ServicesMap servicesMap = new ServicesMap();
		SService sService = new SService("", org.bimserver.shared.interfaces.ServiceInterface.class);
		servicesMap.add(sService);
		servicesMap.add(new SService("", NotificationInterface.class, Collections.singletonList(sService)));
		
		ReflectorBuilder reflectorBuilder = new ReflectorBuilder(servicesMap);
		ReflectorFactory reflectorFactory = reflectorBuilder.newReflectorFactory();
		ServiceInterface createReflector = reflectorFactory.createReflector(ServiceInterface.class, new Reflector() {
			
			@Override
			public Object callMethod(String interfaceName, String methodName, Class<?> definedReturnType, KeyValuePair... args) throws ServerException, UserException {
				return null;
			}
		});
		
		System.out.println(createReflector);
	}
	
	@SuppressWarnings("unchecked")
	public ReflectorFactory newReflectorFactory() {
		implementationCounter++;
		try {
			pool = ClassPool.getDefault();
			pool.insertClassPath(new ClassClassPath(this.getClass()));
			
			for (String name : servicesMap.keySetName()) {
				SService sService = servicesMap.getByName(name);
				build1((Class<? extends PublicInterface>) Class.forName(name), sService);
				build2((Class<? extends PublicInterface>) Class.forName(name), sService);
			}
			
			CtClass reflectorFactoryImpl = pool.makeClass("org.bimserver.reflector.ReflectorFactoryImpl" + implementationCounter);
			reflectorFactoryImpl.addInterface(pool.get(ReflectorFactory.class.getName()));
			
			createCreateReflectorMethod1(reflectorFactoryImpl);
			createCreateReflectorMethod2(reflectorFactoryImpl);
			
			Class<?> class1 = pool.toClass(reflectorFactoryImpl, getClass().getClassLoader(), getClass().getProtectionDomain());
			return (ReflectorFactory) class1.newInstance();
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return null;
	}

	private void createCreateReflectorMethod2(CtClass reflectorFactoryImpl) throws NotFoundException, CannotCompileException {
		CtClass[] parameters = new CtClass[2];
		parameters[0] = pool.get("java.lang.Class");
		parameters[1] = pool.get("org.bimserver.shared.interfaces.PublicInterface");
		CtMethod method = new CtMethod(pool.get("org.bimserver.shared.reflector.Reflector"), "createReflector", parameters, reflectorFactoryImpl);
		StringBuilder methodBuilder = new StringBuilder();
		methodBuilder.append("{");
		methodBuilder.append("if (1==0) {");
		for (String name : servicesMap.keySetName()) {
			SService sService = servicesMap.getByName(name);
			methodBuilder.append("} else if ($1.getSimpleName().equals(\"" + sService.getSimpleName() + "\")) {");
			methodBuilder.append("return new " + GENERATED_CLASSES_PACKAGE + "." + sService.getSimpleName() + "Reflector" + implementationCounter + "((" + name + ")$2);");
		}
		methodBuilder.append("}");
		methodBuilder.append("return null;");
		methodBuilder.append("}");
		method.setBody(methodBuilder.toString());
		reflectorFactoryImpl.addMethod(method);
	}

	private void createCreateReflectorMethod1(CtClass reflectorFactoryImpl) throws NotFoundException, CannotCompileException {
		CtClass[] parameters = new CtClass[2];
		parameters[0] = pool.get("java.lang.Class");
		parameters[1] = pool.get("org.bimserver.shared.reflector.Reflector");
		CtMethod method = new CtMethod(pool.get("org.bimserver.shared.interfaces.PublicInterface"), "createReflector", parameters, reflectorFactoryImpl);
		StringBuilder methodBuilder = new StringBuilder();
		methodBuilder.append("{");
		methodBuilder.append("if (1==0) {");
		for (String name : servicesMap.keySetName()) {
			SService sService = servicesMap.getByName(name);
			methodBuilder.append("} else if ($1.getSimpleName().equals(\"" + sService.getSimpleName() + "\")) {");
			methodBuilder.append("return new " + GENERATED_CLASSES_PACKAGE + "." + sService.getSimpleName() + "Impl" + implementationCounter + "($2);");
		}
		methodBuilder.append("}");
		methodBuilder.append("return null;");
		methodBuilder.append("}");
		method.setBody(methodBuilder.toString());
		reflectorFactoryImpl.addMethod(method);
	}
	
	public void build1(Class<? extends PublicInterface> interfaceClass, org.bimserver.shared.meta.SService sService) {
		try {
			CtClass reflectorImplClass = pool.makeClass(GENERATED_CLASSES_PACKAGE + "." + interfaceClass.getSimpleName() + "Impl" + implementationCounter);
			reflectorImplClass.addInterface(pool.get(interfaceClass.getName()));
			CtClass reflectorClass = pool.get("org.bimserver.shared.reflector.Reflector");
			CtField reflectorField = new CtField(reflectorClass, "reflector", reflectorImplClass);
			reflectorImplClass.addField(reflectorField);
			CtConstructor constructor = new CtConstructor(new CtClass[] {reflectorClass}, reflectorImplClass);
			StringBuilder sb = new StringBuilder();
			reflectorImplClass.addConstructor(constructor);
			sb.append("{");
			sb.append("this.reflector = $1;");
			sb.append("}");
			constructor.setBody(sb.toString());
			
			for (SMethod sMethod : sService.getMethods()) {
				CtClass[] parameters = new CtClass[sMethod.getParameters().size()];
				int i=0;
				for (org.bimserver.shared.meta.SParameter sParameter : sMethod.getParameters()) {
					parameters[i] = pool.get(sParameter.getType().toJavaCode());
					i++;
				}
				CtMethod method = new CtMethod(pool.get(sMethod.getReturnType().getName()), sMethod.getName(), parameters, reflectorImplClass);
				StringBuilder methodBuilder = new StringBuilder();
				methodBuilder.append("{");
				if (sMethod.getReturnType().isVoid()) {
				} else {
					methodBuilder.append("return (" + sMethod.getReturnType().getName() + ")");
				}
				methodBuilder.append("reflector.callMethod(\"" + interfaceClass.getSimpleName() + "\", \"" + sMethod.getName() + "\", " + sMethod.getReturnType().getName() + ".class");
				if (sMethod.getParameters().isEmpty()) {
					methodBuilder.append(", new org.bimserver.shared.reflector.KeyValuePair[0]");
				} else {
					methodBuilder.append(", new org.bimserver.shared.reflector.KeyValuePair[]{");
					int x=1;
					for (SParameter sParameter : sMethod.getParameters()) {
						methodBuilder.append("new org.bimserver.shared.reflector.KeyValuePair(\"" + sParameter.getName() + "\", $" + x + ")");
						if (sMethod.getParameter(sMethod.getParameters().size() - 1) != sParameter) {
							methodBuilder.append(", ");
						}
						x++;
					}
					methodBuilder.append("}");
				}
				methodBuilder.append(");");
				methodBuilder.append("}");
				method.setBody(methodBuilder.toString());
				reflectorImplClass.addMethod(method);
			}
			pool.toClass(reflectorImplClass, getClass().getClassLoader(), getClass().getProtectionDomain());
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}
	
	public void build2(Class<? extends PublicInterface> interfaceClass, org.bimserver.shared.meta.SService sService) {
		try {
			CtClass reflectorImplClass = pool.makeClass(GENERATED_CLASSES_PACKAGE + "." + interfaceClass.getSimpleName() + "Reflector" + implementationCounter);
			CtClass reflectorClass = pool.get("org.bimserver.shared.reflector.Reflector");
			CtClass interfaceCtClass = pool.get(interfaceClass.getName());
			reflectorImplClass.addInterface(reflectorClass);
			CtField reflectorField = new CtField(interfaceCtClass, "publicInterface", reflectorImplClass);
			reflectorImplClass.addField(reflectorField);
			CtConstructor constructor = new CtConstructor(new CtClass[] {interfaceCtClass}, reflectorImplClass);
			StringBuilder sb = new StringBuilder();
			reflectorImplClass.addConstructor(constructor);
			sb.append("{");
			sb.append("this.publicInterface = $1;");
			sb.append("}");
			constructor.setBody(sb.toString());

			CtClass[] parameters = new CtClass[4];
			parameters[0] = pool.get("java.lang.String");
			parameters[1] = pool.get("java.lang.String");
			parameters[2] = pool.get("java.lang.Class");
			parameters[3] = pool.get("org.bimserver.shared.reflector.KeyValuePair[]");
			CtMethod method = new CtMethod(pool.get("java.lang.Object"), "callMethod", parameters, reflectorImplClass);

			StringBuilder methodBuilder = new StringBuilder();
			methodBuilder.append("{");
			methodBuilder.append("if  (1==0) {} ");
			for (SMethod sMethod : sService.getMethods()) {
				methodBuilder.append(" else if ($2.equals(\"" + sMethod.getName() + "\")) {");
				if (!sMethod.getReturnType().isVoid()) {
					methodBuilder.append("return ");
				}
				methodBuilder.append("publicInterface." + sMethod.getName() + "(");
				int i=0;
				for (SParameter sParameter : sMethod.getParameters()) {
					methodBuilder.append("(" + sParameter.getType().toJavaCode() + ")$4[" + i + "].getValue()");
					if (i < sMethod.getParameters().size() - 1) {
						methodBuilder.append(", ");
					}
					i++;
				}
				methodBuilder.append(");");
				methodBuilder.append("}");
			}
			methodBuilder.append("return null;");
			methodBuilder.append("}");
			method.setBody(methodBuilder.toString());
			reflectorImplClass.addMethod(method);
			
			pool.toClass(reflectorImplClass, getClass().getClassLoader(), getClass().getProtectionDomain());
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}
}