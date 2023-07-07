PROCEDURE     GET_TRAN_IDS
(
	CURRENT_TRAN_ID 		IN 	COS_BPR_DATA.TRANSACTION_ID_2%TYPE,
	CURRENT_TRAN_LINK_ID 	IN 	COS_BPR_DATA.TRANSACTION_LINK_ID_2%TYPE,
	NEW_TRAN_ID 			OUT COS_BPR_DATA.TRANSACTION_ID%TYPE,
	NEW_TRAN_LINK_ID 		OUT COS_BPR_DATA.TRANSACTION_LINK_ID%TYPE
)
IS

	REC_COS_BPR_DATA_ID   		COS_BPR_DATA%ROWTYPE := NULL;
	REC_COS_BPR_DATA_LINK_ID    COS_BPR_DATA%ROWTYPE := NULL;


BEGIN

	SELECT *
	INTO REC_COS_BPR_DATA_ID
	FROM cos_bpr_data
	WHERE record_date >= sysdate - 2
	AND TRANSACTION_ID_2 = CURRENT_TRAN_ID
	AND TRANSACTION_LINK_ID_2 = CURRENT_TRAN_LINK_ID
	AND TRANSACTION_ID IS NOT NULL
	AND TRANSACTION_LINK_ID IS NOT NULL
	AND ROWNUM = 1;

	NEW_TRAN_ID := REC_COS_BPR_DATA_ID.TRANSACTION_ID;
	NEW_TRAN_LINK_ID :=  REC_COS_BPR_DATA_ID.TRANSACTION_LINK_ID;

EXCEPTION
  WHEN NO_DATA_FOUND THEN

  		BEGIN

			SELECT *
			INTO REC_COS_BPR_DATA_LINK_ID
			FROM cos_bpr_data
			WHERE record_date >= sysdate - 2
            AND TRANSACTION_LINK_ID_2 = CURRENT_TRAN_LINK_ID
			AND TRANSACTION_LINK_ID IS NOT NULL
			AND ROWNUM = 1;

	   		NEW_TRAN_ID := BPR_TRAN_SEQ.GET_NEXT_SEQUENCE();
	   		NEW_TRAN_LINK_ID :=  REC_COS_BPR_DATA_LINK_ID.TRANSACTION_LINK_ID;

  		EXCEPTION
  		WHEN NO_DATA_FOUND THEN

  			NEW_TRAN_LINK_ID := BPR_TRAN_SEQ.GET_NEXT_SEQUENCE();
			NEW_TRAN_ID := BPR_TRAN_SEQ.GET_NEXT_SEQUENCE();

 		END;

END GET_TRAN_IDS;
