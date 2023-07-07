define institution_number = '00000111';
SELECT p_chgbk.transaction_slip, sundry.sundry_transaction_slip
FROM (
		SELECT orig_pres.transaction_slip, COUNT(orig_pres.transaction_slip) number_of_partials
		from int_sundry_transactions snd_trn,
			 int_transactions orig_pres
		where
		orig_pres.institution_number = &institution_number
		and orig_pres.transaction_category = '001' -- ORIGINAL PRESENTMENT
		and orig_pres.reversal_flag = '000' --NOT REVERSED

		and snd_trn.transaction_slip = orig_pres.transaction_slip
		and snd_trn.sundry_type = '001' -- 1ST CHARGEBACK SUNDRY RECORD
		and to_number(snd_trn.tran_amount_gr) < to_number(orig_pres.tran_amount_gr) -- PARTIAL AMOUNT

		group by orig_pres.transaction_slip
	)p_chgbk,
	int_sundry_transactions sundry
WHERE number_of_partials > 1 -- MULTIPLE PARTIALS
and sundry.transaction_slip = p_chgbk.transaction_slip
and sundry.sundry_type = '001'
UNION
SELECT p_chgbk.transaction_slip, sundry.sundry_transaction_slip
FROM (
		SELECT orig_pres.transaction_slip, COUNT(orig_pres.transaction_slip) number_of_partials
		from int_sundry_transactions snd_trn,
			 int_transactions orig_pres
		where
		orig_pres.institution_number = &institution_number
		and orig_pres.transaction_category = '001' -- ORIGINAL PRESENTMENT
		and orig_pres.reversal_flag = '000' --NOT REVERSED

		and snd_trn.transaction_slip = orig_pres.transaction_slip
		and snd_trn.sundry_type = '002' -- 2ND CHARGEBACK SUNDRY RECORD
		and to_number(snd_trn.tran_amount_gr) < to_number(orig_pres.tran_amount_gr) -- PARTIAL AMOUNT

		group by orig_pres.transaction_slip
	)p_chgbk,
	int_sundry_transactions sundry
WHERE number_of_partials > 1 -- MULTIPLE PARTIALS
and sundry.transaction_slip = p_chgbk.transaction_slip
and sundry.sundry_type = '002'
order by 1,2
;
