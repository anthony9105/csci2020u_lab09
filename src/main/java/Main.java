/**
 Anthony Liscio
 Lab 09
 */

import javafx.application.Application;;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Main extends Application
{
    String[] dates = new String[72];
    Group root = new Group();

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception
    {
        XYChart.Series stock1 = new XYChart.Series();
        XYChart.Series stock2 = new XYChart.Series();
        String saveFileName1 = "GOOG.csv";
        String tickerSymbol1 = "GOOG";
        String saveFileName2 = "AAPL.csv";
        String tickerSymbol2 = "AAPL";

        // download/create/save the stock prices from the url to a csv file
        downloadStockPrices(saveFileName1, tickerSymbol1);
        downloadStockPrices(saveFileName2, tickerSymbol2);

        // x axis
        NumberAxis xAxis = new NumberAxis(1, 72, 1);

        // y axis
        NumberAxis yAxis = new NumberAxis(0f, 800f, 20f);
        yAxis.setLabel("Stock close price ($)");

        LineChart lineChart = new LineChart(xAxis, yAxis);
        stock1.setName(tickerSymbol1);
        stock2.setName(tickerSymbol2);

        // load data from the csv files for the lineChart
        loadData(saveFileName1, lineChart, stock1);
        loadData(saveFileName2, lineChart, stock2);

        xAxis.setLabel("Months (from "+dates[0]+" to "+dates[dates.length-1]+")");

        lineChart.setPrefSize(800, 600);
        Group root = new Group(lineChart);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setHeight(700);
        primaryStage.setWidth(900);
        primaryStage.setTitle("Lab 09: Stock Performance");
        primaryStage.show();
    }

    /**
     * downloadStockPrices method used to download a csv file of a stock's data from 2010 to 2015
     * @param saveFileName - String file name to save this as
     * @param tickerSymbol - String ticker symbol of the stock
     */
    public void downloadStockPrices(String saveFileName, String tickerSymbol)
    {
        try
        {
            // download ans dave file from a URL
            URL url = new URL("https://query1.finance.yahoo.com/v7/finance/download/" + tickerSymbol +
                    "?period1=1262322000&period2=1451538000&interval=1mo&events=history&includeAdjustedClose=true");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(false);
            conn.setDoInput(true);
            InputStream is = conn.getInputStream();

            // filepath for saving/creating the file
            String saveFilePath = System.getProperty("user.dir");
            saveFilePath += "/src/main/resources/" + saveFileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            // write to the file
            int BUFFER_SIZE = 4096;
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = is.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, bytesRead);
            }

            // close streams
            outputStream.close();
            is.close();
        }
        catch(MalformedURLException mue)
        {
            System.out.println("Malformed URL Exception caught");
        }
        catch(IOException io)
        {
            System.out.println("IO Exception caught");
        }
    }

    /**
     * loadData method used to load the data from the csv file
     * @param fileName - String csv file name
     * @param lineChart - the LineChart
     * @param stock - XYChart.Series representing one of the stocks in the line chart
     */
    public void loadData(String fileName, LineChart lineChart, XYChart.Series stock)
    {
        try {
            // filepath
            String csvFilePath = System.getProperty("user.dir");
            csvFilePath += "/src/main/resources/" + fileName;

            // using a BufferedReader to read line-by-line from the file
            FileReader fileReader = new FileReader(csvFilePath);
            BufferedReader input = new BufferedReader(fileReader);

            String line = null;
            int i = 0;

            // loop to go through each line in the CSV file
            while ((line = input.readLine()) != null)
            {
                // split the String line into a String array using commas as delimiters
                String [] lineArray = line.split(",");

                if (i != 0)
                {
                    dates[i-1] = lineArray[0];
                    // adding the data point to the XYChart.Series.
                    // lineArray[4] is the stock close price and i + 1 is the month number (starting from January 2010)
                    stock.getData().add(new XYChart.Data(i + 1, Float.parseFloat(lineArray[4])));
                }
                i++;    // update the iterator variable
            }
            // add the updated XYChart.Series to the line chart
            lineChart.getData().add(stock);
        }
        catch(IOException io)
        {
            System.out.println("IO Exception caught");
        }
    }
}
