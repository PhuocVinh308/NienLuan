package com.mycompany.hamiltoniancyclefinder;

   class Edge {
        String startNodeName;
        String endNodeName;
        double edgeLength;

        public Edge(String startNodeName, String endNodeName, double edgeLength) {
            this.startNodeName = startNodeName;
            this.endNodeName = endNodeName;
            this.edgeLength = edgeLength;
        }
    }