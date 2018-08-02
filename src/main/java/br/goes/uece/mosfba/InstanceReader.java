package br.goes.uece.mosfba;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

public class InstanceReader {
	
	private BufferedReader reader;
	private int numberOfRequirements;
	private int numberOfCustomers;
	
	public InstanceReader(File instance){
		try{
			this.reader = new BufferedReader(new FileReader(instance));
			reader.mark(294967296);
			numberOfRequirements = getNumberOfRequirements();
			numberOfCustomers = getNumberOfCustomers();
		}
		catch(Exception e){
			System.out.println("Instance reading error");
			e.printStackTrace();
		}
	}
	
	public int getNumberOfCustomers(){
		String[] values = null;
		int numberOfCustomers = 0;
		
		try{
			reader.reset();
			values = reader.readLine().split(" ");
			numberOfCustomers = Integer.parseInt(values[0]);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return numberOfCustomers;
	}

	public int getNumberOfRequirements(){
		String[] values = null;
		int numberOfRequirements = 0;
		
		try{
			reader.reset();
			values = reader.readLine().split(" ");
			numberOfRequirements = Integer.parseInt(values[1]);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return numberOfRequirements;
	}

	
	public double[] getCustomersImportance(){
		double[] customersImportance = new double[numberOfCustomers];
		String[] values = null;
		
		try{
			reader.reset();
			reader.readLine();
			reader.readLine();
			values = reader.readLine().split(" ");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		for(int i = 0; i <= customersImportance.length - 1; i++){
			customersImportance[i] = Double.parseDouble(values[i]);
		}
		
		return customersImportance;
	}
	public int[][] getPrecendenceMatrix(){
		int[][] precedences = new int[numberOfRequirements][numberOfRequirements];
		String[] values = null;
		int n = numberOfRequirements;
		try{
			reader.reset();
			reader.readLine();
			reader.readLine();
			reader.readLine();
			reader.readLine();
			for (int i = 0; i < n; i++) {
				values = reader.readLine().split(" ");
				for (int j = 0; j < n; j++) {
					precedences[i][j] = Byte.parseByte(values[j]);
				}
			}	
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		return precedences;
	}
	public double[][] getCvalueMatrix(){
		double[][] cvalue = new double[numberOfRequirements][numberOfRequirements];
		String[] values = null;
	
		try{
			reader.reset();
			reader.readLine();
			reader.readLine();
			reader.readLine();
			reader.readLine();
			for (int i = 0; i < numberOfRequirements+1; i++) {
				reader.readLine();
			}
			for (int i = 0; i < numberOfRequirements; i++) {
				values = reader.readLine().split(" ");
				for (int j = 0; j < numberOfRequirements; j++) {
					cvalue[i][j] = Double.parseDouble(values[j]);
				}
			}	
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		return cvalue;
	}
	
	public double[][] getIcostMatrix(){
		double[][] icost = new double[numberOfRequirements][numberOfRequirements];
		String[] values = null;
	
		try{
			reader.reset();
			reader.readLine();
			reader.readLine();
			reader.readLine();
			reader.readLine();
			for (int i = 0; i < 2*numberOfRequirements+2; i++) {
				reader.readLine();
			}
			for (int i = 0; i < numberOfRequirements; i++) {
				values = reader.readLine().split(" ");
				for (int j = 0; j < numberOfRequirements; j++) {
					icost[i][j] = Double.parseDouble(values[j]);
				}
			}	
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		return icost;
	}
	public double[][] getRequirementsImportances(){
		double[][] requirementsImportances = new double[numberOfCustomers][numberOfRequirements];
		String[] values = null;

		try{
			reader.reset();
			reader.readLine();
			reader.readLine();
			reader.readLine();
			reader.readLine();
			for (int i = 0; i < 3*numberOfRequirements+3; i++) {
				reader.readLine();
			}
			
			for(int i = 0; i < numberOfCustomers; i++){
				values = reader.readLine().split(" ");	
				for(int j = 0; j < values.length; j++){
					requirementsImportances[i][j] = Integer.parseInt(values[j]);
				}
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return requirementsImportances;
	}
	
	public int[] getRequirementsCosts(){
		int[] requirementsCosts = new int[numberOfRequirements];
		
		String[] values = null;
		
		try{
			reader.reset();
			reader.readLine();
			reader.readLine();
			reader.readLine();
			reader.readLine();
			for (int i = 0; i < 3*numberOfRequirements+3; i++) {
				reader.readLine();
			}
			
			for(int i = 0; i < numberOfCustomers; i++){
				reader.readLine();		
			}
			reader.readLine();
			values = reader.readLine().split(" ");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		for(int i = 0; i <= requirementsCosts.length - 1; i++){
			requirementsCosts[i] = Integer.parseInt(values[i]);
		}
		
		return requirementsCosts;
	}
}