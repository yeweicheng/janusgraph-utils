/*******************************************************************************
 *   Copyright 2017 IBM Corp. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package com.ibm.janusgraph.utils.importer.schema;

import com.ibm.janusgraph.utils.schema.JanusGraphSONSchema;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.janusgraph.core.ConfiguredGraphFactory;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.graphdb.management.JanusGraphManager;

public class SchemaLoader {

    public SchemaLoader() {
    }

    public void loadSchema(JanusGraph g, String schemaFile) throws Exception {
        JanusGraphSONSchema importer = new JanusGraphSONSchema(g);
        importer.readFile(schemaFile);
    }

    public static void main(String[] args) throws Exception {

        if (null == args || args.length < 3) {
            System.err.println("Usage: SchemaLoader <janusgraph-config-file> <schema-file> [graph-name]");
            System.exit(1);
        }

        String configFile = args[0];
        String schemaFile = args[1];
        String graphName = args[2];

        // use custom or default config file to get JanusGraph
        JanusGraph graph;
        if (configFile.endsWith(".json")) {
            graph = JanusGraphFactory.open(configFile);
        } else {
            new JanusGraphManager(Settings.read(configFile));
            try {
                ConfiguredGraphFactory.drop(graphName);
            } catch (Exception e) {
                if (!(e instanceof NullPointerException)) {
                    throw e;
                }
            }
            graph = ConfiguredGraphFactory.create(graphName);
        }

        try {
            new SchemaLoader().loadSchema(graph, schemaFile);
        } catch (Exception e) {
            System.out.println("Failed to import schema due to " + e.getMessage());
        } finally {
            graph.close();
            System.exit(0);
        }

    }

}
