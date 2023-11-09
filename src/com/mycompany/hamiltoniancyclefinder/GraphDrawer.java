package com.mycompany.hamiltoniancyclefinder;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

public class GraphDrawer extends JFrame {
    private String startNodeDFS = "";

    private Map<String, Point> nodes = new HashMap<>();
    private Map<String, List<Edge>> edges = new HashMap<>();
    private JTextArea dfsOrderTextArea;

    private Set<String> visitedNodes = new HashSet<>();

    public GraphDrawer() {
        setTitle("Niên luận ngành Kỹ thuật phần mềm ");
        setSize(1080, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        GraphPanel panel = new GraphPanel();
        add(panel, BorderLayout.CENTER);

        
        JButton checkHamiltonianButton = new JButton("Kiểm tra chu trình Hamilton");
        checkHamiltonianButton.addActionListener(e -> checkHamiltonianCycle());
       
      

        
        JButton checkConnectivityButton = new JButton("Kiểm tra tính liên thông");
        checkConnectivityButton.addActionListener(e -> checkConnectivity());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(checkConnectivityButton);


        JButton dfsButton = new JButton("Duyệt DFS từ đỉnh:");
        JTextField startNodeField = new JTextField(10);
        dfsButton.addActionListener(e -> {
            startNodeDFS = startNodeField.getText();
            if (!startNodeDFS.isEmpty()) {
                performDFS(startNodeDFS, panel.getGraphics());
                repaint();
            }
        });

        JButton saveGraphButton = new JButton("Lưu đồ thị");
        saveGraphButton.addActionListener(e -> saveGraphImage(panel));
        buttonPanel.add(saveGraphButton);

        JPanel inputPanel = new JPanel();
        inputPanel.add(dfsButton);
        inputPanel.add(startNodeField);
        buttonPanel.add(inputPanel);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
        
         buttonPanel.add(checkHamiltonianButton);

        JButton readGraphButton = new JButton("Đọc đồ thị từ file");
       
        readGraphButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "Vui lòng nội dung file phải đúng cú pháp: Đỉnh,Đỉnh,Độ dài cung !!!");

            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                readGraphFromFile(selectedFile);
                repaint();
            }
        });
       
        buttonPanel.add(readGraphButton);

    }

private void checkHamiltonianCycle() {
    StringBuilder resultBuilder = new StringBuilder();
    if (hasHamiltonianCycle()) {
        for (String node : nodes.keySet()) {
            List<String> path = new ArrayList<>();
            Set<String> visited = new HashSet<>();
            path.add(node);
            visited.add(node);
            if (findHamiltonianPath(node, path, visited)) {
                for (String visitedNode : path) {
                    resultBuilder.append(visitedNode).append(" => ");
                }
                resultBuilder.append(path.get(0)); // Add the starting node again for the cycle
                break;
            }
        }
    } else {
        resultBuilder.append("Không tìm thấy chu trình Hamilton");
    }
    JOptionPane.showMessageDialog(this, "Thứ tự duyệt chu trình Hamilton: " + resultBuilder.toString());
}

 
 private boolean hasHamiltonianCycle() {
    if (nodes.size() < 3) {
        return false; // Đồ thị phải có ít nhất 3 đỉnh để có chu trình Hamilton
    }
    for (String node : nodes.keySet()) {
        List<String> path = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        path.add(node);
        visited.add(node);
        if (findHamiltonianPath(node, path, visited)) {
            return true;
        }
    }
    return false;
}

private boolean findHamiltonianPath(String currentNode, List<String> path, Set<String> visited) {
    if (path.size() == nodes.size()) {
        String startNode = path.get(0);
        for (Edge edge : edges.get(currentNode)) {
            if (edge.endNodeName.equals(startNode)) {
                return true;
            }
        }
        return false;
    }

    for (Edge edge : edges.get(currentNode)) {
        String nextNode = edge.endNodeName;
        if (!visited.contains(nextNode)) {
            path.add(nextNode);
            visited.add(nextNode);
            if (findHamiltonianPath(nextNode, path, visited)) {
                return true;
            }
            path.remove(path.size() - 1);
            visited.remove(nextNode);
        }
    }
    return false;
}


    private void checkConnectivity() {
        if (isConnected()) {
            JOptionPane.showMessageDialog(this, "Đồ thị là đồ thị liên thông");
        } else {
            JOptionPane.showMessageDialog(this, "Đồ thị không liên thông");
        }
    }
    
    private void readGraphFromFile(File file) {
    try {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                String startNode = parts[0].trim();
                String endNode = parts[1].trim();
                double edgeLength = Double.parseDouble(parts[2].trim());
                nodes.putIfAbsent(startNode, new Point());
                nodes.putIfAbsent(endNode, new Point());
                if (edges.get(startNode) == null) {
                    edges.put(startNode, new ArrayList<>());
                }
                edges.get(startNode).add(new Edge(startNode, endNode, edgeLength));
                if (edges.get(endNode) == null) {
                    edges.put(endNode, new ArrayList<>());
                }
                edges.get(endNode).add(new Edge(endNode, startNode, edgeLength));
            }
        }
        reader.close();
   GraphPanel panel = new GraphPanel();
        add(panel, BorderLayout.CENTER);
        repaint();
    } catch (IOException e) {
        e.printStackTrace();
    }
    repaint();
}


    private void saveGraphImage(Component panel) {
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        panel.printAll(g2);
        g2.dispose();

        try {
            File file = new File("DoThi.png");
            ImageIO.write(image, "png", file);
            JOptionPane.showMessageDialog(this, "Đã lưu đồ thị thành công vào tệp DoThi.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void performDFS(String startNode, Graphics g) {
    Set<String> visited = new HashSet<>();
    Stack<String> stack = new Stack<>();
    StringBuilder dfsOrder = new StringBuilder();
    stack.push(startNode);
    while (!stack.isEmpty()) {
        String currentNode = stack.pop();
        visited.add(currentNode);
        dfsOrder.append(currentNode).append(" => ");
        System.out.println("Visiting node: " + currentNode);
        try {
            highlightNode(g, currentNode, Color.RED, 1000); // Highlight the node for 1 second
            visitedNodes.add(currentNode);
            Thread.sleep(500); // Wait for 1 second
            visitedNodes.remove(currentNode); // Remove the node from the visited set
            repaint();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Traverse unvisited neighbors
        List<Edge> connectedEdges = edges.get(currentNode);
        if (connectedEdges != null) {
            for (Edge edge : connectedEdges) {
                if (!visited.contains(edge.endNodeName)) {
                    stack.push(edge.endNodeName);
                }
            }
        }
    }
    String dfsOrderString = dfsOrder.toString();
    JOptionPane.showMessageDialog(this, "Thứ tự DFS là: " + dfsOrderString.substring(0, dfsOrderString.length() - 4));
}


    private void highlightNode(Graphics g, String nodeName, Color color, long duration) {
        Point nodePoint = nodes.get(nodeName);
        if (nodePoint != null) {
            g.setColor(color);
            g.fillOval(nodePoint.x - 10, nodePoint.y - 10, 20, 20);
            g.drawString(nodeName, nodePoint.x - 5, nodePoint.y - 15);
            try {
                Thread.sleep(duration); // Add a delay to see the changes clearly
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            repaint();
        }
    }

    private boolean isConnected() {
        if (nodes.size() == 0) {
            return false;
        }

        Set<String> visited = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        String startNode = nodes.keySet().iterator().next();
        queue.add(startNode);
        visited.add(startNode);

        while (!queue.isEmpty()) {
            String node = queue.remove();
            List<Edge> connectedEdges = edges.get(node);
            if (connectedEdges != null) {
                for (Edge edge : connectedEdges) {
                    if (!visited.contains(edge.endNodeName)) {
                        visited.add(edge.endNodeName);
                        queue.add(edge.endNodeName);
                    }
                }
            }
        }

        return visited.size() == nodes.size();
    }

    class GraphPanel extends JPanel {
         private Point startNode;
    private Point endNode;
    private String startNodeName;
        public GraphPanel() {
            setBackground(Color.WHITE);
            
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        String nodeName = JOptionPane.showInputDialog("Nhập tên đỉnh cần xóa:");
                        if (nodeName != null) {
                            nodes.remove(nodeName);
                            removeEdges(nodeName);
                            repaint();
                        }
                    } else {
                        String nodeName = JOptionPane.showInputDialog("Mời bạn nhập tên của đỉnh:");
                        if (nodeName != null) {
                            Point newNode = e.getPoint();
                            nodes.put(nodeName, newNode);
                            if (nodes.size() > 1) {
                                String[] nodeNames = nodes.keySet().toArray(new String[0]);
                                String startNodeName = (String) JOptionPane.showInputDialog(null, "Chọn đỉnh bắt đầu:", "Chọn đỉnh", JOptionPane.QUESTION_MESSAGE, null, nodeNames, nodeNames[0]);
                                if (startNodeName != null) {
                                    double edgeLength = Double.parseDouble(JOptionPane.showInputDialog("Mời bạn nhập độ dài của cung:"));
                                    if (edges.get(startNodeName) == null) {
                                        edges.put(startNodeName, new ArrayList<>());
                                    }
                                    edges.get(startNodeName).add(new Edge(startNodeName, nodeName, edgeLength));
                                    if (edges.get(nodeName) == null) {
                                        edges.put(nodeName, new ArrayList<>());
                                    }
                                    edges.get(nodeName).add(new Edge(nodeName, startNodeName, edgeLength));
                                }
                            }
                        }
                        repaint();
                    }
                }
            });
            
        }

        private void removeEdges(String nodeName) {
            edges.remove(nodeName);
            for (List<Edge> edgeList : edges.values()) {
                edgeList.removeIf(edge -> edge.endNodeName.equals(nodeName) || edge.startNodeName.equals(nodeName));
            }
        }

        private void showRemoveEdgeDialog() {
            String startNode = JOptionPane.showInputDialog("Nhập tên đỉnh đầu của cung:");
            String endNode = JOptionPane.showInputDialog("Nhập tên đỉnh cuối của cung:");
            if (startNode != null && endNode != null) {
                removeEdge(startNode, endNode);
            }
        }

        private void removeEdge(String startNode, String endNode) {
            edges.get(startNode).removeIf(edge -> edge.endNodeName.equals(endNode));
            edges.get(endNode).removeIf(edge -> edge.endNodeName.equals(startNode));
            repaint();
        }

   @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    int centerX = getWidth() / 2;
    int centerY = getHeight() / 2;
    int radius = (int) (Math.min(getWidth(), getHeight()) * 0.4);

    int nodeCount = nodes.size();
    for (int i = 0; i < nodeCount; i++) {
        double angle = 2 * Math.PI * i / nodeCount;
        int x = (int) (centerX + radius * Math.cos(angle));
        int y = (int) (centerY + radius * Math.sin(angle));
        String nodeName = (String) nodes.keySet().toArray()[i];
        Point node = new Point(x, y);
        nodes.put(nodeName, node);
        if (visitedNodes.contains(nodeName)) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.BLUE);
        }
        g.fillOval(x - 10, y - 10, 20, 20);
        g.drawString(nodeName, x - 5, y - 15);
    }

    for (Map.Entry<String, List<Edge>> entry : edges.entrySet()) {
        String startNodeName = entry.getKey();
        Point startPoint = nodes.get(startNodeName);
        for (Edge edge : entry.getValue()) {
            String endNodeName = edge.endNodeName;
            Point endPoint = nodes.get(endNodeName);

            g.setColor(Color.BLUE);
            g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
            g.drawString(String.valueOf(edge.edgeLength), (startPoint.x + endPoint.x) / 2, (startPoint.y + endPoint.y) / 2);
        }
    }
}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GraphDrawer::new);
    }
}
