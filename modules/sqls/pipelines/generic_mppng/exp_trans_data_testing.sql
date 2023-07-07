Create or replace Procedure exp_trans_data_testing(file_type varchar2,  inst_no varchar2, card_no varchar2, trans_status varchar2) Is

    out_file utl_file.file_type;
    v_string varchar2(4000);
    filename varchar2(100);      
    test_var varchar2(100);

  -- get the data using cursor
  Cursor c_trans Is
	SELECT
		value_date,
		settlement_date,
		transaction_status,
		institution_number,
		transaction_slip,
		summary_settlement,
		number_original_slip
	FROM
		int_transactions
	WHERE
		institution_number =  inst_no AND
		card_number = card_no AND
	    transaction_status = trans_status;

Begin

	SELECT to_char(sysdate, 'yyyymmddhh24miss')  || '.'|| file_type
	INTO   filename
	FROM dual;      
	
  
   out_file := utl_file.fopen('D:\teste\myexcel.txt', filename, 'W');     --<<<<<<<< path on DB server, or else if the path is on local hard disk, DBAs to execute CREATE OR REPLACE DIRECTORY EXTRACT_DIR AS 'C:\myDir\extract';
																	----<<<<<< GRANT READ, WRITE ON DIRECTORY EXTRACT_DIR TO BW3;
   
                                  
   
-- if you do not want heading then remove below two lines
    v_string := 'value_date, settlement_date, transaction_status, institution_number, transaction_slip, summary_settlement, number_original_slip';
    utl_file.put_line(out_file, v_string);    
   
    -- open the cursor and concatenate fields using comma
    For cur In c_trans Loop
        v_string := cur.value_date
                    || ','
                    || cur.settlement_date
                    || ','
                    || cur.transaction_status
                    || ','
                    || cur.institution_number
                    || ','
                    || cur.transaction_slip
                    || ','
                    || cur.summary_settlement
                    || ','                    
                    || cur.number_original_slip;        
                    
           dbms_output.put_line(v_string);
        -- write each row
        utl_file.put_line(out_file, v_string);
    End Loop;      
    
    -- close the file
    utl_file.fclose(out_file);
Exception
    When Others Then   
    	BEGIN
	        -- on error, close the file if open
	        If utl_file.is_open(out_file) Then
	            utl_file.fclose(out_file);
	        End If;
	        dbms_output.put_line('test failed');
	    END;
End;


