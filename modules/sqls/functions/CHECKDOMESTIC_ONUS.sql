FUNCTION "CHECKDOMESTIC_ONUS" (cardno_in VARCHAR2)
RETURN VARCHAR2
/*
*Created: Carlos Gevido
*Date: 13.02.2002
*Last Revision:14.02.2002
*
*Use by report view to check if the card is Domestic or Onus. Inorder
*to identify we use the card's BIN in conjunction with the BIN length.
*The ordering from the longer BIN to the lowest is important
*to identify the correct ones.
*
*Values which can be passed for parameter owner_in: 'ONUS', 'DOM','INT', 'ONUSDOM'
*
*
*/
IS
	CURSOR DOM_ONUS_cur is
		select /*+ cache(cbr_service_definition) */ 'ONUS' OWNER,
			start_bin_value,end_bin_value,bin_length
		from bw3.cbr_service_definition
		union
      	select /*+ cache(sys_domestic_bin_table) */ 'DOME' OWNER,
      		start_bin_value,end_bin_value,bin_length
		from bw3.sys_domestic_bin_table
		order by bin_length desc;


	DOM_ONUS_rec DOM_ONUS_cur%rowtype;

	record_found VARCHAR2(4) := 'INTL';

BEGIN
	OPEN DOM_ONUS_cur;

	LOOP

		FETCH DOM_ONUS_cur INTO DOM_ONUS_rec;
		EXIT WHEN DOM_ONUS_cur%NOTFOUND;

		if left(cardno_in,to_number(trim(DOM_ONUS_rec.bin_length)))
				BETWEEN DOM_ONUS_rec.start_bin_value AND DOM_ONUS_rec.start_bin_value
		/*
			Check if the BIN of the card can be found in the Domestic or Onus.
			The loop is designed to skip the rest when the BIN is found to speed
			up the execution.
		*/
		THEN
			IF trim(DOM_ONUS_rec.owner) = 'DOME' THEN
				/* Check if the card is whether ONUS or Domestic */
				record_found := 'DOME';
				EXIT;
			Elsif trim(DOM_ONUS_rec.owner) = 'ONUS' THEN
				/* Checks if the is wheter ONUS or Domestic */
				record_found := 'ONUS';
				EXIT;
			END if;
		END IF;
	END LOOP;

	CLOSE DOM_ONUS_cur;
	RETURN record_found;

EXCEPTION
	WHEN OTHERS
		THEN
			dbms_output.put_line(SQLCODE||' '||SQLERRM);
END ;