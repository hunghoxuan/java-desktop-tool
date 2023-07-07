Function CheckRejectTran (TransactionCategory IN VARCHAR2,
                                            TransactionSource IN VARCHAR2,
											OriginalSlip IN VARCHAR2,
                                            StartDate IN VARCHAR2,
                                            InstitutionNumber IN VARCHAR2)
RETURN Number

/*
Function to be used by the quarterly statistics for checking whether to include/exclude reprocessed transactions,
depending on when the original transaction has been sent to card scheme, i.e. whether during this period or not.
*/

IS
    v_return Boolean;
    v_num NUMBER := 0;
	v_record_date VARCHAR2(8) := null;

Begin

	--only applied to presentments
	If TransactionCategory <> '001' Then
		v_return := True;
	Else
		--only applied to interchange rejects
		If TransactionSource <> '037' Then
			v_return := True;
		Else
			--SQL retrieving the date of the original presentment, in case of interchange rejects.
			Select MIN(RECORD_DATE)
			Into v_record_date
			From int_transactions
			Where institution_number = InstitutionNumber
			and transaction_source <> '037'
			START WITH transaction_slip = OriginalSlip
			and institution_number = InstitutionNumber
			connect by prior number_Original_Slip = transaction_slip
			and PRIOR NUMBER_ORIGINAL_SLIP <> PRIOR TRANSACTION_SLIP
			and PRIOR institution_number = INSTITUTION_NUMBER;

			--If the record date of the original presentment is not in this quarter, discard the transaction
			--from this quarter.
			If v_record_date >= StartDate Then
				v_return := True;
			Else
				v_return := False;
			End if;
	    End If;
	End if;

	--boolean is not supported in SQL & thus has to convert it to numbers
	If v_return = true Then
	    v_num := 1;
	Else
	    v_num := 0;
	End if;

	Return (v_num);

END CheckRejectTran;