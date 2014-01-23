package org.bimserver.ifcengine;

/******************************************************************************
 * Copyright (C) 2009-2014  BIMserver.org
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
 * 
 * BIMserver software uses the IFC Engine DLL library build.
 * This binary Engine DLL is NOT open source. The IFC Engine DLL is a closed source product owned by the Netherlands Organisation for Applied Scientifc Research TNO.
 * The latest versions are available for download from http://ifcengine.com.
 * In case of none commercial use there is no license fee and redistribution of the binary is allowed as long as clearly mentioned that the IFC Engine DLL is used. The BIMserver.org software is non-commercial so users of the BIMserver software can use it free of charge.
 * Within the Open Source BIMserver software there is one exception to the normal conditions: A special version of the IFC Engine DLL is used that includes Clashdetection functionality, this version is not commercially available. For more information, please contact the owner at info@ifcengine.com
 *****************************************************************************/

import org.bimserver.plugins.renderengine.RenderEngineException;
import org.bimserver.plugins.renderengine.RenderEngineInstance;
import org.bimserver.plugins.renderengine.RenderEngineInstanceVisualisationProperties;

public class JvmIfcEngineInstance implements RenderEngineInstance {
	private final JvmIfcEngine failSafeIfcEngine;
	private final int instanceId;
	private final int modelId;

	public JvmIfcEngineInstance(JvmIfcEngine failSafeIfcEngine, int modelId, int instanceId) {
		this.failSafeIfcEngine = failSafeIfcEngine;
		this.modelId = modelId;
		this.instanceId = instanceId;
	}

	public RenderEngineInstanceVisualisationProperties getVisualisationProperties() throws RenderEngineException {
		synchronized (failSafeIfcEngine) {
			failSafeIfcEngine.writeCommand(Command.GET_VISUALISATION_PROPERTIES);
			failSafeIfcEngine.writeInt(modelId);
			failSafeIfcEngine.writeInt(instanceId);
			failSafeIfcEngine.flush();
			return new RenderEngineInstanceVisualisationProperties(failSafeIfcEngine.readInt(), failSafeIfcEngine.readInt(), failSafeIfcEngine.readInt());
		}
	}

	@Override
	public float[] getTransformationMatrix() throws RenderEngineException {
		synchronized (failSafeIfcEngine) {
			failSafeIfcEngine.writeCommand(Command.GET_TRANSFORMATION_MATRIX);
			failSafeIfcEngine.writeInt(modelId);
			failSafeIfcEngine.writeInt(instanceId);
			failSafeIfcEngine.flush();
			float[] result = new float[16];
			for (int i=0; i<16; i++) {
				result[i] = failSafeIfcEngine.readFloat();
			}
			return result;
		}
	}
}