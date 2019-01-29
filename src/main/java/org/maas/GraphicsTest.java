package org.maas;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class GraphicsTest extends Application implements Runnable{

    @Override
    public void start(Stage primaryStage) throws IOException {
        System.out.println("Trying to start...really, I am!");
        /*
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
        */

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of Jobs Completed");
        xAxis.setLabel("Time (Minutes)");
        StackedAreaChart<Number, Number> sac =
                new StackedAreaChart<Number, Number>(xAxis, yAxis);

        primaryStage.setTitle("primary Stage Title");
        sac.setTitle("Job Completion Over Time");
        XYChart.Series<Number, Number> kneadingMachineChart =
                new XYChart.Series<Number, Number>();
        kneadingMachineChart.setName("Kneading Machine Agent");
        kneadingMachineChart.getData().add(new XYChart.Data(0, 0));

        XYChart.Series<Number, Number> doughPrepTableChart =
                new XYChart.Series<Number, Number>();
        doughPrepTableChart.setName("Dough Preperation Table Agent");
        doughPrepTableChart.getData().add(new XYChart.Data(0, 0));

        XYChart.Series<Number, Number> prooferChart =
                new XYChart.Series<Number, Number>();
        prooferChart.setName("Proofer Agent");
        prooferChart.getData().add(new XYChart.Data(0, 0));

        //seriesMay.getData().add(new XYChart.Data(31, 26));

        String FILE_NAME = "mas_maas_log.csv";

        int kneadingMachineTotal = 0;
        int doughPrepTableTotal = 0;
        int prooferTotal = 0;
        int lastTime = 0;
        // now try to read the file
        Scanner scanner = new Scanner(new File(FILE_NAME));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.length() > 1) {
                List<String> logInfo = Arrays.asList(line.split(","));
                if (logInfo.size() > 1) {

                    if (logInfo.get(1).contains("KneadingMachineAgent"))
                    {
                        kneadingMachineChart.getData().add(new XYChart.Data(Integer.parseInt(logInfo.get(0)), ++kneadingMachineTotal));
                    }
                    else if (logInfo.get(1).contains("DoughPrepTableAgent"))
                    {
                        doughPrepTableChart.getData().add(new XYChart.Data(Integer.parseInt(logInfo.get(0)), ++doughPrepTableTotal));
                    }
                    else if (logInfo.get(1).contains("Proofer"))
                    {
                        prooferChart.getData().add(new XYChart.Data(Integer.parseInt(logInfo.get(0)), ++prooferTotal));
                    }
                    /*
                    for (String word : commaSeperated) {
                        System.out.println(word);
                    }
                    */
                    lastTime = Integer.parseInt(logInfo.get(0));
                }
            }
        }
        scanner.close();

        System.out.println(lastTime);
        kneadingMachineChart.getData().add(new XYChart.Data(lastTime, kneadingMachineTotal));
        doughPrepTableChart.getData().add(new XYChart.Data(lastTime, doughPrepTableTotal));
        prooferChart.getData().add(new XYChart.Data(lastTime, prooferTotal));


        Scene scene = new Scene(sac, 800, 600);
        sac.getData().addAll(kneadingMachineChart, doughPrepTableChart, prooferChart);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.out.println(args);
        System.out.println(args.toString());
        //System.out.println(args[0]);
        for (String word : args)
        {
            System.out.println(word);
        }
        System.out.println("Launching with args");
        launch(args);
    }

    @Override
    public void run() {
        System.out.println("\n\n\nI tried to run!\n\n\n");
        String[] args = new String[] {""};
        GraphicsTest.main(args);
    }
}
