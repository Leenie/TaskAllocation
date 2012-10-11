
public class HungarianScheduling {

	public int[][] assignment_array;

	HungarianScheduling(int[][] matrix) // square array passed through parameters by the
	{// VALUING class
		// FETCH AND INITIALISE ARRAY

		// ASSUME ARRAY IS SQUARE (Square array passed to it)
		System.out.println("There are "+matrix.length+" tasks and employees. This is the value matrix:");
		for(int i = 0; i < matrix.length; ++i){
			for(int j = 0; j < matrix[i].length; ++j){
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}


		assignment_array = new int[matrix.length][2];
		// 2 arrays: assignment_array array, 
		// and ARRAY array (combinations)

		System.out.println("The assignment_array matrix will now be calculated...");
		assignment_array = step_zero(matrix); // Call Hungarian algorithm and produce the assignment_array
		System.out.println("This is the assignment_array matrix");
		for(int i = 0; i < assignment_array.length; ++i){
			for(int j = 0; j < assignment_array[i].length; ++j){
				System.out.print(assignment_array[i][j] + " ");
			}
			System.out.println();
		}


	} // END OF CONSTRUCTOR

	public int[][] step_zero(int[][] matrix) {
		System.out.println("Step 0: ");



		int maximum_cost = 0; // find maximum cost
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (matrix[i][j] > maximum_cost) {
					maximum_cost = matrix[i][j];
				}
			}
		}

		System.out.println("Maximum cost in cost matrix is: "+maximum_cost);

		int[][] possible_allocations = new int[matrix.length][matrix.length]; // The possible_allocations(masK) array.
		
		int[] coverRow = new int[matrix.length]; // track rows covered
		int[] coverColumn = new int[matrix.length]; // track columns covered
		int[] zero_position = new int[2]; // for step 4 and 5
		int step = 1;
		boolean done = false;
		while (done == false) // main execution loop
		{
			switch (step) {
			case 1:
				step = step_one(step, matrix);
				break;
			case 2:
				step = step_two(step, matrix, possible_allocations, coverRow, coverColumn);
				break;
			case 3:
				step = step_three(step, possible_allocations, coverColumn);
				break;
			case 4:
				step = step_four(step, matrix, possible_allocations, coverRow, coverColumn,
						zero_position);
				break;
			case 5:
				step = step_five(step, possible_allocations, coverRow, coverColumn,
						zero_position);
				break;
			case 6:
				step = step_six(step, matrix, coverRow, coverColumn, maximum_cost);
				break;
			case 7:
				done = true;
				break;
			}
		}// end while

		int[][] assignment_array = new int[matrix.length][2]; // Create the returned
		// array.
		for (int i = 0; i < possible_allocations.length; i++) {
			for (int j = 0; j < possible_allocations[i].length; j++) {
				if (possible_allocations[i][j] == 1) {
					assignment_array[i][0] = i;
					assignment_array[i][1] = j;
				}
			}
		}

		
		return assignment_array; //using possible_allocations
	}

	public int step_one(int step, int[][] matrix) {

		System.out.println("Step 1: Scan each row and find the smallest element and subtract" +
				"\n"+"it from every element that belongs to the same row");

		int minimum_element;


		for (int i = 0; i < matrix.length; i++) {
			minimum_element = matrix[i][0];
			int j;
			for ( j = 0 ; j < matrix[i].length; j++) // search for the minimum element in the row
				
			{
				if (minimum_element > matrix[i][j]) {
					minimum_element = matrix[i][j];
				}
			}

			System.out.println("Minimum value in row "+j+" is "+minimum_element); //print minimum value in the row

			for (j = 0; j < matrix[i].length; j++) //subtract minimum element from each element in the row
				
			{
				matrix[i][j] = matrix[i][j] - minimum_element;
			}
		}

		step = 2;
		System.out.println("Go to step "+step);
		return step;
	}

	public int step_two(int step, int[][] matrix, int[][] possible_allocations, int[] coverRow,
			int[] coverColumn) {
		
		System.out.println("Step 2: Search for all the zeros in the matrix," +
				"\n"+"cover the row/column where the zero was located");

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if ((matrix[i][j] == 0) && (coverColumn[j] == 0)
						&& (coverRow[i] == 0)) {
					possible_allocations[i][j] = 1;
					coverColumn[j] = 1;
					coverRow[i] = 1;

					System.out.println("Zero found; column and row covered; recorded in mask matrix");
				}
			}
		}

		
		System.out.println("Uncover all rows and columns to  = the number of starred zeros" +
				"\n"+" found in (desirable allocations) in Step 3");

		for (int i = 0; i < coverRow.length; i++) {
			coverRow[i] = 0;
		}
		for (int j = 0; j < coverColumn.length; j++) {
			coverColumn[j] = 0;
		}

		step = 3;
		System.out.println("Go to Step "+step);
		return step;
	}

	public int step_three(int step, int[][] possible_allocations, int[] coverColumn) {
		
		System.out.println("Step 3: Cover the columns containing starred zero's" +
				"\n"+"(recorded in the Mask matrix); starred zeros are desirable allocations");

		for (int i = 0; i < possible_allocations.length; i++) // each column of starred zeros, cover it
		{
			for (int j = 0; j < possible_allocations[i].length; j++) {
				if (possible_allocations[i][j] == 1) { //if prime
					coverColumn[j] = 1; //cover
				}
			}
		}

		int column_count = 0;
		for (int j = 0; j < coverColumn.length; j++) { // check how many columns are covered
			
			if (coverColumn[j] == 1) {
				++column_count;
			}
		}

		if (column_count >= possible_allocations.length) {
			step = 7; //if the numver of cloumns is equal to the number of columns in the matrix we are done
			System.out.println("All columns are covered, therefore all assignments are made (FINISHED)");
		} else {
			step = 4; // else go to step 4, to calculate all assignment_arrays
			System.out.println("NOT all columns are covered, therefore further assignments need to be made");
		}
		System.out.println("Go to step "+step);
		return step;
	}

	public int step_four(int step, int[][] matrix, int[][] possible_allocations, int[] coverRow,
			int[] coverColumn, int[] zero_positions) {
		System.out.print("Step 4: Find starred zeros (desirable allocations) and unstarred zeros(primed zero, an alternative allocation = 2nd choice" +
				"\n in order to find the most preferred assignments");
		
		int[] rowColumn = new int[2]; // stored zero positions (uncovered)
		boolean done = false;
		
		System.out.println("Find uncovered zeros to prime them");
		while (!done) { // search for zeros that are uncovered
			
			rowColumn[0] = -1;
			rowColumn[1] = 0;

			int i = 0;
			boolean finished = false;
			
			while (!finished) { // search for uncovered zeros now starts
				int j = 0;
				while (j < matrix[i].length) { // search through matrix
					if (matrix[i][j] == 0 && coverRow[i] == 0
							&& coverColumn[j] == 0) {
						rowColumn[0] = i; // location of uncovered zero in row
						rowColumn[1] = j; // location of uncovered zero in column
						finished = true;
					}
					++j;
				}
				i = i + 1;
				if (i >= matrix.length) { // if matrix has been searched out
					finished = true; 
				}
			} //search fo uncovered zeros now ends and the locations are stored in  int[][] rowColumn 
		

			if (rowColumn[0] == -1) {
				done = true;
				step = 6;
				System.out.println("There are starred zeros in the same row;" +
						"\n The row has been covered and the column uncovered of the starred zero;" +
						"\n done for all zeros, until none is uncovered." +
						"\n All positions saved" +
				"\n Go to step 6");
			} else {
				possible_allocations[rowColumn[0]][rowColumn[1]] = 2; // no zero was covered
				//prime it in the possible_allocation matrix.

				boolean star_exists_row = false;
				for (int j = 0; j < possible_allocations[rowColumn[0]].length; j++) {
					if (possible_allocations[rowColumn[0]][j] == 1) // the zero was covered
					{
						star_exists_row = true;
						rowColumn[1] = j; // store the location of the zero
					}
				}

				if (star_exists_row == true) { 
					coverRow[rowColumn[0]] = 1; //cover row
					coverColumn[rowColumn[1]] = 0; // uncover the column
				} else { // the zeros locations will be stored
					zero_positions[0] = rowColumn[0];
					zero_positions[1] = rowColumn[1];
					done = true;
					step = 5;
					System.out.println("There are no starred zeros in the same row, go to step 5");

				}
			}
		}
		
		System.out.println("Going to step "+step);
		return step;
	}

	public int step_five(int step, int[][] possible_allocations, int[] coverRow,
			int[] coverColumn, int[] zero_positions) {
		System.out.println("Step 5: ");

		System.out.println("Augmenting path algorithm: " +
				"1) Get a sequence of alternating primes and stars. "+
				"\n2) Get a primed zero"+
				"\n3) Get a starred zero from same column as primed zero"+
				"\n4) Get primed zero in same row as starred zero"+
				"\n5) Find primed zero in that row that doesn't have a starred zero in that column"+
				"\n6) unstar all starred zeros, and star the primes of the series"+
				"\n7) Erase all other primes and reset column and row covers"+
				"\n8) go to step 3) to cover the columns that have starred zero's");
		
		int row_count = 0; // number of rows in the route matrix
		int[][] route = new int[(possible_allocations[0].length + 2)][2]; // store the row and column of path matrix
		route[row_count][0] = zero_positions[0]; // store location of the row of the last prime
		route[row_count][1] = zero_positions[1]; // store the location of the column of last prime

		boolean done = false;
		while (done == false) {

			System.out.println("Finding starred zero in column.");
			int col = route[row_count][1];
			int r = -1; // Again this is a check value.
			for (int i = 0; i < possible_allocations.length; i++) {
				if (possible_allocations[i][col] == 1) {
					r = i;
				}
			}
			// end find starred 0
			System.out.println("Starred zero is in mask["+r+"]["+col+"]");

			if (r >= 0) {
				row_count = row_count + 1;
				route[row_count][0] = r; // Row of starred zero.
				route[row_count][1] = route[row_count - 1][1]; // Column of starred zero.
			} else {
				done = true;
			}

			if (done == false) {
				// search for a zero that is primed
				System.out.println("Find primed zero in row..");
				int current_row = route[row_count][0];

				int c = -1;
				for (int j = 0; j < possible_allocations[current_row].length; j++) {
					if (possible_allocations[current_row][j] == 2) {
						c = j;
					}
				}
				// stop the search for 0's that are primed

				row_count = row_count + 1;
				route[row_count][0] = route[row_count - 1][0]; // Row of primed zero.
				route[row_count][1] = c; // Col of primed zero.
			}
		}// while loop ends here

		System.out.println("Invert masked bits which show what assignments to make:");
		// invert possible_allocationsed bits which show what assignment_arrays to make
		for (int i = 0; i <= row_count; i++) {
			if (possible_allocations[(route[i][0])][(route[i][1])] == 1) {
				possible_allocations[(route[i][0])][(route[i][1])] = 0;
			} else {
				possible_allocations[(route[i][0])][(route[i][1])] = 1;
			}
		} // end

		// reset all covers
		System.out.println("Reset all row and column covers");
		for (int i = 0; i < coverRow.length; i++) {
			coverRow[i] = 0;
		}
		for (int j = 0; j < coverColumn.length; j++) {
			coverColumn[j] = 0;
		}
		// end

		// remove all primed 0s
		System.out.println("Remove all primed zeros");

		for (int i = 0; i < possible_allocations.length; i++) {
			for (int j = 0; j < possible_allocations[i].length; j++) {
				if (possible_allocations[i][j] == 2) {
					possible_allocations[i][j] = 0;
				}
			}
		}
		// end

		step = 3;
		System.out.println("Go to step "+step);
		return step;

	}

	public int step_six(int step, int[][] matrix, int[] coverRow,
			int[] coverColumn, int max_cost) {
		
		System.out.println("Step 6: added returned value from Step 4, to every element" +
				"\n of each covered row and subtract from every uncovered column");

		// find smallest uncovered value
		System.out.println("Find smallest uncovered value");
		int minval = max_cost; // start from here
		for (int i = 0; i < matrix.length; i++) // work down to find smallest value
		{
			for (int j = 0; j < matrix[i].length; j++) {
				if (coverRow[i] == 0 && coverColumn[j] == 0
						&& (minval > matrix[i][j])) {
					minval = matrix[i][j];
				}
			}
		}

		for (int i = 0; i < coverRow.length; i++) {
			for (int j = 0; j < coverColumn.length; j++) {
				// now add minimum value to every element of the covered rows
				System.out.println("Adding minimum value to every element of the covered rows");
				if (coverRow[i] == 1) {
					matrix[i][j] = matrix[i][j] + minval;
				}
				// subtract from every element of the uncovered columns
				System.out.println("Subtracting from every element of the uncovered columns");

				if (coverColumn[j] == 0) {
					matrix[i][j] = matrix[i][j] - minval;
				}
			}
		}

		step = 4;
		System.out.println("Go to step "+step);
		return step;
	}

}
