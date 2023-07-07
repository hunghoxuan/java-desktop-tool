CREATE OR REPLACE PACKAGE BODY
  -------------------------------------------------------------------
  --   (C) 2019 by RS2Group International.
  --
  --   NAME: BW_DISPUTES.pkb
  --
  --   DESCRIPTION: Package implementation for disputes.
  --
  --   VERSION: 3.3.5
  --
  --   DATE: 08 February 2023
  --
  --   NOTES: This package provides the implementation to filter and agregate disputes.
  -------------------------------------------------------------------

BW_DISPUTES AS

  FUNCTION get_exclude_actions(p_exclude_type NUMBER)
    RETURN BW_DISPUTES_ARRAY AS
      v_excluded_actions BW_DISPUTES_ARRAY;
    BEGIN
      IF p_exclude_type = c_non_case_impacting THEN
        v_excluded_actions := BW_DISPUTES_ARRAY(
          c_dispute_query_action, --'230'
          c_attach_document_action, --'106'
          c_attach_internal_document_action, --'244'
          c_dispute_tran_action, --'101'
          c_transfer_to_new_user, --'112'
          c_escalate_case, --'242'
          c_escalation_handled, --'243'
          c_provided_documentation, --'245'
          c_add_internal_notes, --'246'
          c_chargeback_fee, --'115'
          c_retrieval_fee, --'116'
          c_no_documentation_to_send_merchant, --'262'
          c_acquirer_sent_message, --'266'
          c_merchant_sent_message, --'267'
          c_system_generated_document, -- '268'
          c_add_document_to_case_storage, --'269'
          c_unshared_document,    --'270'
          c_manager_release_lock_action, -- '272'
          c_agent_release_lock_action, -- '273'
          c_locked_case_next_action, -- '274'
          c_locked_case_action, -- '275'
          c_pend_case_action, -- '276'
          c_un_pend_case_action, -- '277'
          c_manager_assigned_case_action, -- '278'
          c_manager_unassigned_case_action, -- '279'
          c_auto_release_lock_action, -- '280'
          c_operator_released_action -- '284'
        );
      ELSIF p_exclude_type = c_non_merchant_viewable THEN
        v_excluded_actions := BW_DISPUTES_ARRAY(
          c_escalate_case, --'242'
          c_escalation_handled, --'243'
          c_attach_internal_document_action, --'244'
          c_add_internal_notes, --'246'
          c_case_header_action, --'110'
          c_add_document_to_case_storage, --'269'
          c_unshared_document,  --'270'
          c_manager_release_lock_action, --'272'
          c_agent_release_lock_action, --'273'
          c_locked_case_next_action, --'274'
          c_locked_case_action, --'275'
          c_pend_case_action, --'276'
          c_un_pend_case_action, --'277'
          c_manager_assigned_case_action, --'278'
          c_manager_unassigned_case_action, --'279'
          c_auto_release_lock_action, --'280'
          c_transfer_action, --'100'
          c_reassign_case, --'109'
          c_transfer_to_new_user, --'112'
          c_status_change, --'114'
          c_operator_released_action -- '284'
        );
      ELSIF p_exclude_type = c_non_revertable THEN
        v_excluded_actions := BW_DISPUTES_ARRAY(
          c_miscellaneous_action, --'104'
          c_dispute_query_action, --'230'
          c_attach_document_action, --'106'
          c_attach_internal_document_action, --'244'
          c_dispute_tran_action, --'101'
          c_transfer_to_new_user, --'112'
          c_escalate_case, --'242'
          c_escalation_handled, --'243'
          c_provided_documentation, --'245'
          c_add_internal_notes, --'246'
          c_status_change, --'114'
          c_chargeback_fee, --'115'
          c_retrieval_fee, --'116'
          c_no_documentation_to_send_merchant, --'262'
          c_revert_previous_action, --'263'
          c_acquirer_sent_message, --'266'
          c_merchant_sent_message, --'267'
          c_system_generated_document, -- '268'
          c_add_document_to_case_storage, --'269'
          c_unshared_document,    --'270'
          c_manager_release_lock_action, -- '272'
          c_agent_release_lock_action, -- '273'
          c_locked_case_next_action, -- '274'
          c_locked_case_action, -- '275'
          c_pend_case_action, -- '276'
          c_un_pend_case_action, -- '277'
          c_manager_assigned_case_action, -- '278'
          c_manager_unassigned_case_action, -- '279'
          c_auto_release_lock_action, -- '280'
          c_operator_released_action -- '284'
        );
      ELSE
        v_excluded_actions := BW_DISPUTES_ARRAY();
      END IF;

      RETURN v_excluded_actions;
    END;

  FUNCTION get_max_history_id(p_institution_number INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
                              p_acquirer_reference INT_SUNDRY_HISTORY.ACQUIRER_REFERENCE%TYPE,
                              p_is_portal_request VARCHAR2 DEFAULT 'FALSE')
    RETURN INT_SUNDRY_HISTORY.SUNDRY_HISTORY_ID%TYPE AS
      v_result VARCHAR2(11);
    BEGIN
      SELECT MAX(SUNDRY_HISTORY_ID)
      INTO v_result
        FROM INT_SUNDRY_HISTORY
        WHERE INSTITUTION_NUMBER = p_institution_number
          AND ACQUIRER_REFERENCE = p_acquirer_reference
          AND SUNDRY_TYPE IS NOT NULL
          AND (RULE_ACTION NOT IN (SELECT * FROM TABLE(get_exclude_actions(c_non_case_impacting)))

            AND NOT (RULE_ACTION = c_miscellaneous_action --'104'
                AND CARD_ORGANIZATION = c_discover_card_org --'023'
                AND SUNDRY_TYPE = c_msg_confirmation_sundry_type) --'065'

            AND (
                  (p_is_portal_request = 'FALSE')
                  OR
                  (
                    NOT (RULE_ACTION = c_miscellaneous_action --'104'
                        AND CARD_ORGANIZATION = c_discover_card_org --'023'
                        AND SUNDRY_TYPE = c_rejects) --'013'

                    AND NOT ( RULE_ACTION = c_confirmation_rejected_handled_action) --'247'
                  )
                )
              );

      RETURN v_result;
    END;

  FUNCTION get_last_action_by_actor(p_institution_number    INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
                                    p_case_number         INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
                                    p_actor               BWT_SUNDRY_ACTOR.INDEX_FIELD%TYPE,
                                    p_is_portal_request   VARCHAR2 DEFAULT 'FALSE')
    RETURN ROW_LAST_ACTOR_ACTION AS
      v_result ROW_LAST_ACTOR_ACTION;
    BEGIN
      SELECT HIST.SUNDRY_HISTORY_ID, HIST.RULE_ACTION
      INTO v_result
      FROM INT_SUNDRY_HISTORY HIST
      WHERE HIST.INSTITUTION_NUMBER = p_institution_number
      AND SUNDRY_HISTORY_ID = (
        SELECT MAX(SUNDRY_HISTORY_ID) AS SUNDRY_HISTORY_ID
          FROM INT_SUNDRY_HISTORY
          WHERE INSTITUTION_NUMBER = HIST.INSTITUTION_NUMBER
            AND CASE_NUMBER = p_case_number
            AND ((p_actor = BWTPAD('BWT_SUNDRY_ACTOR', '001') -- merchant
                AND MP_USER_ID IS NOT NULL)
              OR (p_actor = BWTPAD('BWT_SUNDRY_ACTOR', '002') -- operator
                AND USER_ID IS NOT NULL))
            AND SUNDRY_TYPE IS NOT NULL
            AND (RULE_ACTION NOT IN (SELECT * FROM TABLE(get_exclude_actions(c_non_case_impacting)))

              AND NOT (RULE_ACTION = c_miscellaneous_action --'104'
                  AND CARD_ORGANIZATION = c_discover_card_org --'023'
                  AND SUNDRY_TYPE = c_msg_confirmation_sundry_type) --'065'

              AND (
                    (p_is_portal_request = 'FALSE')
                    OR
                    (
                      NOT (RULE_ACTION = c_miscellaneous_action --'104'
                          AND CARD_ORGANIZATION = c_discover_card_org --'023'
                          AND SUNDRY_TYPE = c_rejects) --'013'

                      AND NOT ( RULE_ACTION = c_confirmation_rejected_handled_action) --'247'
                    )
                  )
              )
      );

      RETURN v_result;
    END;

  FUNCTION get_broker_hierarchy_clients(p_institution_number   INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
                                        p_date                 CIS_CLIENT_RELATION_LINKS.EFFECTIVE_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, 'YYYYMMDD'),
                                        p_broker_client_number INT_SUNDRY_HISTORY.CLIENT_NUMBER%TYPE DEFAULT NULL)
    RETURN BW_DISPUTES_ARRAY AS
      v_broker_clients BW_DISPUTES_ARRAY;
    BEGIN
        SELECT REL.CLIENT_NUMBER
        BULK COLLECT INTO v_broker_clients
        FROM CIS_CLIENT_RELATION_LINKS REL
        WHERE REL.INSTITUTION_NUMBER = p_institution_number
          AND REL.DEST_INSTITUTION_NUMBER = p_institution_number
          AND REL.LINK_STATUS = c_client_status_active

          -- get all applicable link types
          AND REL.LINK_TYPE IN (SELECT BWT.INDEX_FIELD
                              FROM BWT_LINK_TYPE BWT,
                                  CIS_CLIENT_DETAILS CIS
                              WHERE BWT.DESTINATION_CLIENT_TYPE = CIS.CLIENT_TYPE
                                AND BWT.INSTITUTION_NUMBER = CIS.INSTITUTION_NUMBER
                                AND CIS.CLIENT_NUMBER = REL.DEST_CLIENT_NUMBER
                                AND CIS.INSTITUTION_NUMBER = REL.DEST_INSTITUTION_NUMBER)

          -- get the latest effective relation link
          AND REL.EFFECTIVE_DATE = (SELECT MAX(LATEST_REL.EFFECTIVE_DATE)
                                    FROM CIS_CLIENT_RELATION_LINKS LATEST_REL
                                    WHERE LATEST_REL.INSTITUTION_NUMBER = REL.INSTITUTION_NUMBER
                                      AND LATEST_REL.LINK_TYPE = REL.LINK_TYPE
                                      AND LATEST_REL.CLIENT_NUMBER = REL.CLIENT_NUMBER
                                      AND LATEST_REL.GROUP_NUMBER = REL.GROUP_NUMBER
                                      AND LATEST_REL.DEST_INSTITUTION_NUMBER = REL.DEST_INSTITUTION_NUMBER
                                      AND LATEST_REL.DEST_CLIENT_NUMBER = REL.DEST_CLIENT_NUMBER
                                      AND LATEST_REL.EFFECTIVE_DATE <= p_date)

          -- iterate recursively through children till none are left to get full hierarchy
          START WITH
          REL.DEST_CLIENT_NUMBER = p_broker_client_number
          AND REL.INSTITUTION_NUMBER = p_institution_number
          CONNECT BY NOCYCLE
          PRIOR REL.CLIENT_NUMBER = REL.DEST_CLIENT_NUMBER

        -- include client number of logged in user since logged in user will not have a
        -- self relation link
        UNION
        SELECT CLIENT_NUMBER
        FROM CIS_CLIENT_DETAILS
        WHERE INSTITUTION_NUMBER = p_institution_number
          AND CLIENT_NUMBER = p_broker_client_number
          AND CLIENT_STATUS = c_client_status_active;

      RETURN v_broker_clients;
    END;

  FUNCTION fetch_all_disputes(p_institution_number      INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
                              p_current_language        VARCHAR2 DEFAULT 'USA',
                              p_default_language        VARCHAR2 DEFAULT 'USA',
                              p_date                    VARCHAR2 DEFAULT TO_CHAR(SYSDATE, 'YYYYMMDD'),
                              p_is_portal_request       VARCHAR2 DEFAULT 'FALSE',
                              p_case_number             INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE DEFAULT NULL,
                              p_owner_id                INT_SUNDRY_HISTORY.OWNER_ID%TYPE DEFAULT NULL,
                              p_user_id                 INT_SUNDRY_HISTORY.USER_ID%TYPE DEFAULT NULL,
                              p_client_numbers          BW_DISPUTES_ARRAY DEFAULT NULL,
                              p_client_number           INT_SUNDRY_HISTORY.CLIENT_NUMBER%TYPE DEFAULT NULL,
                              p_case_status             INT_SUNDRY_HISTORY.CASE_STATUS%TYPE DEFAULT NULL,
                              p_last_action             INT_SUNDRY_HISTORY.RULE_ACTION%TYPE DEFAULT NULL,
                              p_reference_number        INT_SUNDRY_HISTORY.EXTERNAL_CASE_NUMBER%TYPE DEFAULT NULL,
                              p_case_type               INT_SUNDRY_HISTORY.SUNDRY_TYPE%TYPE DEFAULT NULL,
                              p_card_organization       INT_SUNDRY_HISTORY.CARD_ORGANIZATION%TYPE DEFAULT NULL,
                              p_escalated_flag          INT_SUNDRY_HISTORY.ESCALATED_FLAG%TYPE DEFAULT NULL,
                              p_card_number             INT_SUNDRY_TRANSACTIONS.CARD_NUMBER%TYPE DEFAULT NULL,
                              p_amount_from             INT_SUNDRY_TRANSACTIONS.TRAN_AMOUNT_GR%TYPE DEFAULT NULL,
                              p_amount_to               INT_SUNDRY_TRANSACTIONS.TRAN_AMOUNT_GR%TYPE DEFAULT NULL,
                              p_date_from               INT_SUNDRY_TRANSACTIONS.TRANSACTION_DATE%TYPE DEFAULT NULL,
                              p_date_to                 INT_SUNDRY_TRANSACTIONS.TRANSACTION_DATE%TYPE DEFAULT NULL,
                              p_acquirer_reference      INT_SUNDRY_HISTORY.ACQUIRER_REFERENCE%TYPE DEFAULT NULL,
                              p_unread_messages         BWT_CONFIRMATION.INDEX_FIELD%TYPE DEFAULT NULL,
                              p_broker_client_number    INT_SUNDRY_HISTORY.CLIENT_NUMBER%TYPE DEFAULT NULL,
                              p_currency                INT_SUNDRY_TRANSACTIONS.TRAN_CURRENCY%TYPE DEFAULT NULL,
                              p_settlement_currency     INT_SUNDRY_TRANSACTIONS.SETTLEMENT_CURRENCY%TYPE DEFAULT NULL,
                              p_settlement_amount_from  INT_SUNDRY_TRANSACTIONS.SETTLEMENT_AMOUNT_GR%TYPE DEFAULT NULL,
                              p_settlement_amount_to    INT_SUNDRY_TRANSACTIONS.SETTLEMENT_AMOUNT_GR%TYPE DEFAULT NULL,
                              p_group_number            INT_SUNDRY_HISTORY.GROUP_NUMBER%TYPE DEFAULT NULL)
    RETURN TBL_ALL_FETCHED_DISPUTES
    PIPELINED AS
      v_client_filter_count number := 0;

      CURSOR tbl_cursor IS
        /* This WITH clause handles filtering the above matherialized view with
        the client number/s filter if provided */
        WITH HEADER AS (
          SELECT /*+materialize*/ *
            FROM INT_SUNDRY_HISTORY HEADER
            WHERE HEADER.INSTITUTION_NUMBER = p_institution_number
            AND HEADER.RULE_ACTION = c_case_header_action --'110'
            -- cater for optional case number parameter
            AND (p_case_number IS NULL
                OR HEADER.CASE_NUMBER = p_case_number)

            -- cater for optional owner id parameter
            AND (p_owner_id IS NULL
                OR HEADER.OWNER_ID = p_owner_id)

            -- cater for optional user id parameter
            AND (p_user_id IS NULL
                OR HEADER.USER_ID = p_user_id)

            -- cater for optional client number parameter
            AND (p_client_number IS NULL
                OR HEADER.CLIENT_NUMBER = p_client_number)

            -- cater for optional case status parameter
            AND (p_case_status IS NULL
                OR HEADER.CASE_STATUS = p_case_status)

            -- cater for optional external case number parameter
            AND (p_reference_number IS NULL
                OR HEADER.EXTERNAL_CASE_NUMBER = p_reference_number)

            -- cater for optional card organization parameter
            AND (p_card_organization IS NULL
                OR HEADER.CARD_ORGANIZATION = p_card_organization)

            -- cater for optional escalated flag parameter
            AND (p_escalated_flag IS NULL
                OR NVL(HEADER.ESCALATED_FLAG, '000') = p_escalated_flag)

            -- cater for optional acquirer_reference parameter
            AND (p_acquirer_reference IS NULL
                OR HEADER.ACQUIRER_REFERENCE = p_acquirer_reference)

            -- cater for optional group_number parameter
            AND (p_group_number IS NULL
                OR HEADER.GROUP_NUMBER = p_group_number)                     

            -- cater for optional broker client number parameter
            AND (p_broker_client_number IS NULL
                OR HEADER.CLIENT_NUMBER IN (SELECT *
                                            FROM TABLE (get_broker_hierarchy_clients( HEADER.INSTITUTION_NUMBER,
                                                                                      p_date,
                                                                                      p_broker_client_number))))

            AND v_client_filter_count = 0
          UNION ALL
          SELECT *
            FROM INT_SUNDRY_HISTORY HEADER
            WHERE HEADER.INSTITUTION_NUMBER = p_institution_number
            AND HEADER.RULE_ACTION = c_case_header_action --'110'
            -- cater for optional case number parameter
            AND (p_case_number IS NULL
                OR HEADER.CASE_NUMBER = p_case_number)

            -- cater for optional owner id parameter
            AND (p_owner_id IS NULL
                OR HEADER.OWNER_ID = p_owner_id)

            -- cater for optional user id parameter
            AND (p_user_id IS NULL
                OR HEADER.USER_ID = p_user_id)

            -- cater for optional client number parameter
            AND (p_client_number IS NULL
                OR HEADER.CLIENT_NUMBER = p_client_number)

            -- cater for optional case status parameter
            AND (p_case_status IS NULL
                OR HEADER.CASE_STATUS = p_case_status)

            -- cater for optional external case number parameter
            AND (p_reference_number IS NULL
                OR HEADER.EXTERNAL_CASE_NUMBER = p_reference_number)

            -- cater for optional card organization parameter
            AND (p_card_organization IS NULL
                OR HEADER.CARD_ORGANIZATION = p_card_organization)

            -- cater for optional escalated flag parameter
            AND (p_escalated_flag IS NULL
                OR NVL(HEADER.ESCALATED_FLAG, '000') = p_escalated_flag)

            -- cater for optional acquirer_reference parameter
            AND (p_acquirer_reference IS NULL
                OR HEADER.ACQUIRER_REFERENCE = p_acquirer_reference)

            -- cater for optional group_number parameter
            AND (p_group_number IS NULL
                OR HEADER.GROUP_NUMBER = p_group_number)                  

            -- cater for optional broker client number parameter
            AND (p_broker_client_number IS NULL
                OR HEADER.CLIENT_NUMBER IN (SELECT *
                                            FROM TABLE (get_broker_hierarchy_clients( HEADER.INSTITUTION_NUMBER,
                                                                                      p_date,
                                                                                      p_broker_client_number))))

            AND v_client_filter_count > 0
            AND HEADER.CLIENT_NUMBER IN (SELECT COLUMN_VALUE FROM TABLE(p_client_numbers))
        ),
        /* This WITH clause makes use of the fully filtered case headers from
        the above and adds on other required case information */
        CASES AS (
          SELECT  /*+cardinality (HEADER, 10) leading (HEADER, LAST_REC, HIST_LAST_SUNDRY, LAST_SUNDRY_SLIP, SND_TRN, HIST_LAST_CENTRAL, LAST_CENTRAL_DATE, HIST_LAST_CBK_SUNDRY, LAST_MSG_SUNDRY_SLIP) */
                  HEADER.INSTITUTION_NUMBER,
                  HEADER.CASE_NUMBER,
                  HEADER.CLIENT_NUMBER,
                  HEADER.EXTERNAL_CASE_NUMBER,
                  LAST_REC.RECORD_DATE,
                  LAST_REC.SUNDRY_TYPE,
                  HEADER.CASE_STATUS,
                  LAST_REC.RULE_ACTION,
                  SND_TRN.TRANSACTION_DATE,
                  LAST_CENTRAL_DATE.CENTRAL_DATE,
                  SND_TRN.TRAN_CURRENCY,
                  SND_TRN.TRAN_AMOUNT_GR,
                  SND_TRN.SETTLEMENT_CURRENCY,
                  SND_TRN.SETTLEMENT_AMOUNT_GR,
                  HEADER.USER_ID,
                  HEADER.CARD_ORGANIZATION,
                  NVL(HEADER.ESCALATED_FLAG, c_confirmation_no)     AS ESCALATED_FLAG,
                  LAST_REC.SUNDRY_HISTORY_ID,
                  HEADER.ACQUIRER_REFERENCE,
                  NVL(get_chargeback_reason(HEADER.INSTITUTION_NUMBER,
                                            HEADER.ACQUIRER_REFERENCE),
                      c_generic_chargeback_reason)                  AS CHARGEBACK_REASON, --'999'
                  get_dispute_condition(HEADER.INSTITUTION_NUMBER,
                                        HEADER.ACQUIRER_REFERENCE)  AS DISPUTE_CONDITION,
                  get_retrieval_reason(HEADER.INSTITUTION_NUMBER,
                                      HEADER.ACQUIRER_REFERENCE)    AS RETRIEVAL_REASON,
                  DECODE (HEADER.CARD_ORGANIZATION, c_visa_card_org, --'003'
                    is_rapid_dispute_resolution_case(HEADER.CASE_NUMBER, HEADER.INSTITUTION_NUMBER),
                    c_confirmation_no)                              AS RAPID_DISPUTE_RESOLUTION,
                  LAST_SUNDRY_SLIP.CENTRAL_DATE                     AS SUNDRY_CREATION_CENTRAL_DATE,
                  SND_TRN.CARD_NUMBER,
                  HEADER.OWNER_ID,
                  HEADER.GROUP_NUMBER,
                  (SELECT SND_MSG.MESSAGE_SUNDRY_TEXT
                    FROM INT_SUNDRY_TRANSACTIONS SND_MSG
                    WHERE SND_MSG.INSTITUTION_NUMBER = LAST_MSG_SUNDRY_SLIP.INSTITUTION_NUMBER
                    AND SND_MSG.SUNDRY_TRANSACTION_SLIP = LAST_MSG_SUNDRY_SLIP.SUNDRY_TRANSACTION_SLIP
                    AND SND_MSG.SUNDRY_TYPE = LAST_MSG_SUNDRY_SLIP.SUNDRY_TYPE) AS MESSAGE_SUNDRY_TEXT,
                  has_unread_messages(p_institution_number,
                                    HEADER.CASE_NUMBER,
                                    p_is_portal_request)            AS UNREAD_MESSAGES
          FROM
                HEADER,
                INT_SUNDRY_HISTORY LAST_REC,--  last record for case
                INT_SUNDRY_HISTORY LAST_SUNDRY_SLIP, -- last record which has a sundry_trasaction_slip
                INT_SUNDRY_HISTORY LAST_MSG_SUNDRY_SLIP, -- last chargeback record which has a sundry_trasaction_slip
                INT_SUNDRY_HISTORY LAST_CENTRAL_DATE, -- last record which has a central_date
                INT_SUNDRY_TRANSACTIONS SND_TRN

          WHERE LAST_REC.INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
            AND LAST_REC.SUNDRY_HISTORY_ID = get_max_history_id(HEADER.INSTITUTION_NUMBER, HEADER.ACQUIRER_REFERENCE)

            AND LAST_SUNDRY_SLIP.INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
            AND LAST_SUNDRY_SLIP.SUNDRY_HISTORY_ID = (SELECT MAX(SUNDRY_HISTORY_ID)
                                                      FROM INT_SUNDRY_HISTORY HIST_LAST_SUNDRY
                                                      WHERE INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
                                                        AND ACQUIRER_REFERENCE = HEADER.ACQUIRER_REFERENCE
                                                        AND SUNDRY_TRANSACTION_SLIP IS NOT NULL
                                                        AND RULE_ACTION NOT IN (c_chargeback_fee, --'115'
                                                                                c_retrieval_fee)) --'116'

            AND SND_TRN.INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
            AND SND_TRN.SUNDRY_TYPE = LAST_SUNDRY_SLIP.SUNDRY_TYPE
            AND SND_TRN.SUNDRY_TRANSACTION_SLIP = LAST_SUNDRY_SLIP.SUNDRY_TRANSACTION_SLIP

            AND LAST_CENTRAL_DATE.INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
            AND LAST_CENTRAL_DATE.SUNDRY_HISTORY_ID = (SELECT MAX(SUNDRY_HISTORY_ID)
                                                        FROM INT_SUNDRY_HISTORY HIST_LAST_CENTRAL
                                                        WHERE INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
                                                        AND ACQUIRER_REFERENCE = HEADER.ACQUIRER_REFERENCE
                                                        AND CENTRAL_DATE IS NOT NULL)

            AND LAST_MSG_SUNDRY_SLIP.INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
            AND LAST_MSG_SUNDRY_SLIP.SUNDRY_HISTORY_ID = (SELECT MAX(SUNDRY_HISTORY_ID)
                                                            FROM INT_SUNDRY_HISTORY HIST_LAST_CBK_SUNDRY
                                                            WHERE INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
                                                            AND ACQUIRER_REFERENCE = HEADER.ACQUIRER_REFERENCE
                                                            AND SUNDRY_TRANSACTION_SLIP IS NOT NULL
                                                            AND SUNDRY_TYPE IN (c_first_chargeback, -- '001'
                                                                                c_second_chargeback, -- '002'
                                                                                c_retrieval_request, -- '008' -- (to cater for when no chargeback has been raised yet)
                                                                                c_inw_precomp, -- '092' -- (to cater for when a case is opened with an inward pre-comp, thus no chargeback)
                                                                                c_inw_comp, -- '093' -- (to cater for when a case is opened with an inward comp, thus no chargeback)
                                                                                c_out_precomp, -- '416' -- (to cater for when a case is opened with an outward pre-comp, thus no chargeback)
                                                                                c_out_comp, -- '417' -- (to cater for when a case is opened with an outward comp, thus no chargeback)
                                                                                c_pre_arbitration,  -- '095'
                                                                                c_pre_arbitration_decision,  -- '099'
                                                                                c_arbitration, -- '064'
                                                                                c_arbitration_decision, -- '100'
                                                                                c_collaboration_req) -- '103' 
                                                            )

            -- cater for optional last action parameter
            AND (p_last_action IS NULL
                OR LAST_REC.RULE_ACTION = p_last_action)

            -- cater for optional case type parameter
            AND (p_case_type IS NULL
                OR LAST_REC.SUNDRY_TYPE = p_case_type)

            -- cater for optional card number parameter
            AND (p_card_number IS NULL
                OR SND_TRN.CARD_NUMBER = p_card_number)

            -- cater for optional currency parameter
            AND (p_currency IS NULL
                OR SND_TRN.TRAN_CURRENCY = p_currency)

            -- cater for optional amount from parameter
            AND (p_amount_from IS NULL
                OR SND_TRN.TRAN_AMOUNT_GR >= TO_NUMBER(p_amount_from))

            -- cater for optional amount to parameter
            AND (p_amount_to IS NULL
                OR SND_TRN.TRAN_AMOUNT_GR <= TO_NUMBER(p_amount_to))

            -- cater for optional settlement currency parameter
            AND (p_settlement_currency IS NULL
                OR SND_TRN.SETTLEMENT_CURRENCY = p_settlement_currency)

            -- cater for optional settlement amount from parameter
            AND (p_settlement_amount_from IS NULL
                OR SND_TRN.SETTLEMENT_AMOUNT_GR >= TO_NUMBER(p_settlement_amount_from))

            -- cater for optional settlement amount to parameter
            AND (p_settlement_amount_to IS NULL
                OR SND_TRN.SETTLEMENT_AMOUNT_GR <= TO_NUMBER(p_settlement_amount_to))

            -- cater for optional date from parameter
            AND (p_date_from IS NULL
                OR SND_TRN.TRANSACTION_DATE >= TO_NUMBER(p_date_from))

            -- cater for optional date to parameter
            AND (p_date_to IS NULL
                OR SND_TRN.TRANSACTION_DATE <= TO_NUMBER(p_date_to))
        )

        /* This part of the query retrieves its data from the above WITH clauses
        adds more calculated case details and translations where necessary */
        SELECT
                CASES.INSTITUTION_NUMBER                                          AS INSTITUTION_NUMBER,
                CASES.CASE_NUMBER                                                 AS CASE_NUMBER,
                CASES.EXTERNAL_CASE_NUMBER                                        AS REFERENCE_NUMBER,
                CASES.SUNDRY_HISTORY_ID                                           AS SUNDRY_HISTORY_ID,
                CASES.RECORD_DATE                                                 AS LAST_UPDATED,
                CASES.SUNDRY_TYPE                                                 AS CASE_TYPE,
                CASES.CASE_STATUS                                                 AS CASE_STATUS,
                CASE_STATUS.CASE_STATUS                                           AS CASE_STATUS_DESCRIPTION,
                CASES.RULE_ACTION                                                 AS LAST_ACTION,
                NVL(
                  CASES.RETRIEVAL_REASON,
                  CASE CASES.CARD_ORGANIZATION
                  WHEN c_visa_card_org THEN --003
                    /*Visa cases use VCR reason codes, hence dispute_condition.
                    But Visa pin debit disputes still use old reason codes.*/
                    NVL(CASES.DISPUTE_CONDITION, CASES.CHARGEBACK_REASON)
                  ELSE
                    CASES.CHARGEBACK_REASON
                  END)                                                            AS REASON,
                get_reason_description( CASES.INSTITUTION_NUMBER,
                                        CASES.CHARGEBACK_REASON,
                                        CASES.DISPUTE_CONDITION,
                                        CASES.RETRIEVAL_REASON,
                                        p_current_language,
                                        CASES.CARD_ORGANIZATION,
                                        p_default_language)                       AS REASON_DESCRIPTION,
                get_reason_lookup(CASES.INSTITUTION_NUMBER,
                                  CASES.CHARGEBACK_REASON,
                                  CASES.DISPUTE_CONDITION,
                                  CASES.RETRIEVAL_REASON,
                                  CASES.CARD_ORGANIZATION)                        AS REASON_LOOKUP,
                CASES.TRANSACTION_DATE                                            AS TRANSACTION_DATE,
                get_days_to_card_scheme_deadline(CASES.CASE_NUMBER,
                                                CASES.INSTITUTION_NUMBER,
                                                p_date)                           AS DAYS_TO_CARD_SCHEME_DEADLINE,
                CASES.CENTRAL_DATE                                                AS CENTRAL_DATE,
                CASES.TRAN_CURRENCY                                               AS CURRENCY,
                CASES.TRAN_AMOUNT_GR                                              AS AMOUNT,
                CASES.SETTLEMENT_CURRENCY                                         AS SETTLEMENT_CURRENCY,
                CASES.SETTLEMENT_AMOUNT_GR                                        AS SETTLEMENT_AMOUNT,
                CASES.USER_ID                                                     AS USER_ID,
                get_days_to_action(CASES.INSTITUTION_NUMBER,
                              CASES.ACQUIRER_REFERENCE,
                              p_date,
                              CASES.CENTRAL_DATE)                                 AS DAYS_TO_ACTION,
                CASES.CLIENT_NUMBER                                               AS CLIENT_NUMBER,
                CASES.CARD_ORGANIZATION                                           AS CARD_ORGANIZATION,
                CASES.CHARGEBACK_REASON                                           AS CHARGEBACK_REASON,
                CASES.RETRIEVAL_REASON                                            AS RETRIEVAL_REASON,
                CASES.DISPUTE_CONDITION                                           AS DISPUTE_CONDITION,
                CASES.ESCALATED_FLAG                                              AS ESCALATED_FLAG,
                CASES.RAPID_DISPUTE_RESOLUTION                                    AS RAPID_DISPUTE_RESOLUTION,
                CASES.ACQUIRER_REFERENCE                                          AS ACQUIRER_REFERENCE,
                CASES.UNREAD_MESSAGES                                             AS UNREAD_MESSAGES,
                CASES.SUNDRY_CREATION_CENTRAL_DATE                                AS SUNDRY_CREATION_CENTRAL_DATE,
                CASES.CARD_NUMBER                                                 AS CARD_NUMBER,
                CASES.OWNER_ID                                                    AS OWNER_ID,
                CASES.GROUP_NUMBER                                                AS GROUP_NUMBER,
                CASES.MESSAGE_SUNDRY_TEXT                                         AS DISPUTE_MESSAGE
        FROM
              CASES,
              BWT_CASE_STATUS CASE_STATUS

        WHERE CASE_STATUS.INSTITUTION_NUMBER = CASES.INSTITUTION_NUMBER
              AND CASE_STATUS.INDEX_FIELD = CASES.CASE_STATUS
              AND CASE_STATUS.LANGUAGE = CASE
                                        WHEN EXISTS(
                                            SELECT INDEX_FIELD
                                            FROM BWT_CASE_STATUS
                                            WHERE LANGUAGE = p_current_language
                                                  AND INSTITUTION_NUMBER = CASE_STATUS.INSTITUTION_NUMBER
                                                  AND INDEX_FIELD = CASE_STATUS.INDEX_FIELD)
                                          THEN p_current_language
                                        ELSE p_default_language
                                        END

              -- cater for optional unread messages parameter
              AND (p_unread_messages IS NULL
                  OR CASES.UNREAD_MESSAGES = p_unread_messages);

      BEGIN
        -- Get the quantity of client numbers to filter by.
        SELECT COUNT(*)
        INTO v_client_filter_count
        FROM TABLE (p_client_numbers);

        -- Loop through the  cursor and output each record in the pipeline.
        FOR cursor_record IN tbl_cursor LOOP
          PIPE ROW (cursor_record);
        END LOOP;
      END;

  FUNCTION get_all_cases(p_institution_number VARCHAR2, p_current_language VARCHAR2 DEFAULT 'USA',
                      p_default_language   VARCHAR2 DEFAULT 'USA', p_date VARCHAR2 DEFAULT TO_CHAR(SYSDATE, 'YYYYMMDD'),
                      p_is_portal_request VARCHAR2 DEFAULT 'FALSE')
    RETURN TBL_ALL_CASES
    PIPELINED AS
    CURSOR tbl_cursor IS
              SELECT   /*+ cardinality(HEADER 10)*/
                HEADER.INSTITUTION_NUMBER,
                HEADER.CASE_NUMBER,
                HEADER.EXTERNAL_CASE_NUMBER,
                LAST_REC.SUNDRY_HISTORY_ID,
                LAST_REC.RECORD_DATE,
                LAST_REC.SUNDRY_TYPE,
                HEADER.CASE_STATUS,
                LAST_REC.RULE_ACTION,
                SND_TRN.TRANSACTION_DATE,
                LAST_CENTRAL_DATE.CENTRAL_DATE,
                SND_TRN.TRAN_CURRENCY,
                SND_TRN.TRAN_AMOUNT_GR,
                HEADER.USER_ID,
                TRN_BCH.CLIENT_NUMBER,
                TRN_CLR.RETRIEVAL_REFERENCE,
                TRN_CLR.AUTH_CODE,
                TRN_CLR.ACQUIRER_REFERENCE,
                TRN_CLR.VALUE_DATE,
                TRN_CLR.CARD_NUMBER,
                HEADER.CARD_ORGANIZATION,
                SND_NOTE.MESSAGE_SUNDRY_TEXT,
                NVL(get_chargeback_reason(HEADER.INSTITUTION_NUMBER,
                                          HEADER.ACQUIRER_REFERENCE),
                      c_generic_chargeback_reason) AS CHARGEBACK_REASON, --'999'
                get_dispute_condition(HEADER.INSTITUTION_NUMBER,
                                      HEADER.ACQUIRER_REFERENCE) AS DISPUTE_CONDITION,
                get_retrieval_reason(HEADER.INSTITUTION_NUMBER,
                                    HEADER.ACQUIRER_REFERENCE)  AS RETRIEVAL_REASON,
                NVL(HEADER.ESCALATED_FLAG, c_confirmation_no) AS ESCALATED_FLAG,
                DECODE (HEADER.CARD_ORGANIZATION, c_discover_card_org,
                  DECODE (LAST_REC.SUNDRY_TYPE , c_rejects,
                          (SELECT DECODE(SND.MESSAGE_SUNDRY_TEXT, '', '', SUBSTR(SND.MESSAGE_SUNDRY_TEXT, 0, INSTR(SND.MESSAGE_SUNDRY_TEXT, ' ', 1) - 1))
                            FROM INT_SUNDRY_TRANSACTIONS SND,
                                INT_SUNDRY_HISTORY HST
                            WHERE HST.INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
                            AND HST.CASE_NUMBER = HEADER.CASE_NUMBER
                            AND HST.SUNDRY_HISTORY_ID IN (SELECT MAX(SUNDRY_HISTORY_ID)
                                            FROM INT_SUNDRY_HISTORY
                                            WHERE INSTITUTION_NUMBER = HST.INSTITUTION_NUMBER
                                            AND CASE_NUMBER = HST.CASE_NUMBER
                                            AND RULE_ACTION = c_miscellaneous_action
                                            AND SUNDRY_TYPE = c_rejects
                                            AND CARD_ORGANIZATION = c_discover_card_org
                                            AND SUNDRY_TRANSACTION_SLIP IS NOT NULL)
                            AND SND.INSTITUTION_NUMBER = HST.INSTITUTION_NUMBER
                            AND SND.SUNDRY_TRANSACTION_SLIP = HST.SUNDRY_TRANSACTION_SLIP),
                          ''), '') AS INDEX_MESSAGE_FROM_SCHEME,
                DECODE (HEADER.CARD_ORGANIZATION, c_visa_card_org,
                  is_rapid_dispute_resolution_case(HEADER.CASE_NUMBER, HEADER.INSTITUTION_NUMBER),
                  c_confirmation_no) AS RAPID_DISPUTE_RESOLUTION
              FROM
                INT_SUNDRY_HISTORY HEADER,
                INT_SUNDRY_HISTORY LAST_REC,--  last record for case
                INT_SUNDRY_HISTORY LAST_SUNDRY_SLIP, -- last record which has a sundry_trasaction_slip
                INT_SUNDRY_HISTORY LAST_CENTRAL_DATE, -- last record which has a central_date
                INT_TRANSACTIONS TRN_CLR,
                INT_TRANSACTIONS TRN_BCH,
                INT_TRANSACTIONS TRN_LST,
                INT_SUNDRY_TRANSACTIONS SND_NOTE,
                INT_SUNDRY_TRANSACTIONS SND_TRN

              WHERE
                HEADER.INSTITUTION_NUMBER = p_institution_number
                AND HEADER.RULE_ACTION = c_case_header_action

                AND LAST_REC.INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
                AND LAST_REC.SUNDRY_HISTORY_ID = get_max_history_id(HEADER.INSTITUTION_NUMBER, HEADER.ACQUIRER_REFERENCE)

                AND TRN_CLR.INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
                AND TRN_CLR.TRANSACTION_SLIP = HEADER.NUMBER_ORIGINAL_SLIP

                AND TRN_BCH.INSTITUTION_NUMBER = TRN_CLR.INSTITUTION_NUMBER
                AND TRN_BCH.TRANSACTION_SLIP = TRN_CLR.SUMMARY_SETTLEMENT

                AND TRN_LST.INSTITUTION_NUMBER  = TRN_CLR.INSTITUTION_NUMBER
                AND TRN_LST.ACQUIRER_REFERENCE = TRN_CLR.ACQUIRER_REFERENCE
                AND TRN_LST.TRANSACTION_SLIP = (SELECT MAX(TRANSACTION_SLIP) -- this is required to cater for 2nd chargebacks
                                                  FROM INT_TRANSACTIONS
                                                  WHERE INSTITUTION_NUMBER = TRN_LST.INSTITUTION_NUMBER
                                                  AND ACQUIRER_REFERENCE = TRN_LST.ACQUIRER_REFERENCE
                                                  AND TRANSACTION_CATEGORY in ( c_chargeback_category,
                                                                                c_retrieval_category, -- retrieval requests (to cater for when no chargeback has been raised yet)
                                                                                c_presentment_category) -- original presentment (to cater for when a pre-comp is raised prior to us having an open case)
                                                  AND REVERSAL_FLAG = c_confirmation_no)

                AND SND_NOTE.INSTITUTION_NUMBER(+) = TRN_LST.INSTITUTION_NUMBER
                AND SND_NOTE.SUNDRY_TRANSACTION_SLIP(+) = TRN_LST.TRANSACTION_SLIP
                AND SND_NOTE.SUNDRY_TYPE(+) IN (c_first_chargeback,
                                            c_second_chargeback,
                                            c_retrieval_request, -- retrieval requests (to cater for when no chargeback has been raised yet)
                                            c_inw_precomp) -- pre-compliance (to cater for when a pre-comp is raised prior to us having an open case)

                AND LAST_SUNDRY_SLIP.INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
                AND LAST_SUNDRY_SLIP.SUNDRY_HISTORY_ID = (SELECT MAX(SUNDRY_HISTORY_ID)
                                                          FROM INT_SUNDRY_HISTORY
                                                          WHERE INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
                                                              AND CASE_NUMBER = HEADER.CASE_NUMBER
                                                              AND SUNDRY_TRANSACTION_SLIP IS NOT NULL
                                                              AND RULE_ACTION NOT IN (c_chargeback_fee,
                                                                                      c_retrieval_fee))

                AND LAST_CENTRAL_DATE.INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
                AND LAST_CENTRAL_DATE.SUNDRY_HISTORY_ID = (SELECT MAX(SUNDRY_HISTORY_ID)
                                                          FROM INT_SUNDRY_HISTORY
                                                          WHERE INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
                                                              AND CASE_NUMBER = HEADER.CASE_NUMBER
                                                              AND CENTRAL_DATE IS NOT NULL)

                AND SND_TRN.INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
                AND SND_TRN.SUNDRY_TRANSACTION_SLIP = LAST_SUNDRY_SLIP.SUNDRY_TRANSACTION_SLIP
                AND SND_TRN.SUNDRY_TYPE = LAST_SUNDRY_SLIP.SUNDRY_TYPE;

      BEGIN
        FOR cursor_record IN tbl_cursor LOOP
          PIPE ROW (cursor_record);
        END LOOP;
      END;

  FUNCTION get_all_disputes(p_institution_number VARCHAR2, p_current_language VARCHAR2 DEFAULT 'USA',
                            p_default_language   VARCHAR2 DEFAULT 'USA', p_date VARCHAR2 DEFAULT TO_CHAR(SYSDATE, 'YYYYMMDD'),
                            p_is_portal_request VARCHAR2 DEFAULT 'FALSE')
    RETURN TBL_ALL_DISPUTES
    PIPELINED AS
    CURSOR tbl_cursor IS
        SELECT
          CASES.INSTITUTION_NUMBER                                          AS INSTITUTION_NUMBER,
          CASES.CASE_NUMBER                                                 AS CASE_NUMBER,
          CASES.EXTERNAL_CASE_NUMBER                                        AS REFERENCE_NUMBER,
          CASES.SUNDRY_HISTORY_ID                                           AS SUNDRY_HISTORY_ID,
          CASES.RECORD_DATE                                                 AS LAST_UPDATED,
          CASES.SUNDRY_TYPE                                                 AS CASE_TYPE,
          SUN_TYPE.SUNDRY_TYPE                                              AS CASE_TYPE_DESCRIPTION,
          CASES.CASE_STATUS                                                 AS CASE_STATUS,
          CASE_STATUS.CASE_STATUS                                           AS CASE_STATUS_DESCRIPTION,
          CASES.RULE_ACTION                                                 AS DISPUTE_STATUS,
          ACT.ACTION_TAKEN                                                  AS DISPUTE_STATUS_DESCRIPTION,
          NVL(
            CASES.RETRIEVAL_REASON,
            CASE CASES.CARD_ORGANIZATION
            WHEN BWTPAD('BWT_CARD_ORGANIZATION', '003') THEN
              /*Visa cases use VCR reason codes, hence dispute_condition.
              But Visa pin debit disputes still use old reason codes.*/
              NVL(CASES.DISPUTE_CONDITION, CASES.CHARGEBACK_REASON)
            ELSE
              CASES.CHARGEBACK_REASON
            END)                                                            AS REASON,
          GET_REASON_DESCRIPTION( CASES.INSTITUTION_NUMBER,
                                  CASES.CHARGEBACK_REASON,
                                  CASES.DISPUTE_CONDITION,
                                  CASES.RETRIEVAL_REASON,
                                  p_current_language,
                                  CASES.CARD_ORGANIZATION,
                                  p_default_language)                       AS REASON_DESCRIPTION,
          GET_REASON_LOOKUP(CASES.INSTITUTION_NUMBER,
                            CASES.CHARGEBACK_REASON,
                            CASES.DISPUTE_CONDITION,
                            CASES.RETRIEVAL_REASON,
                            CASES.CARD_ORGANIZATION)                        AS REASON_LOOKUP,
          CASES.TRANSACTION_DATE                                            AS TRANSACTION_DATE,
          get_days_to_card_scheme_deadline(CASES.CASE_NUMBER,
                                          p_institution_number,
                                          p_date)                          AS DAYS_TO_CARD_SCHEME_DEADLINE,
          CASES.CENTRAL_DATE                                                AS CENTRAL_DATE,
          CASES.TRAN_CURRENCY                                               AS CURRENCY,
          CURR.SWIFT_CODE                                                   AS CURRENCY_CODE,
          CASES.TRAN_AMOUNT_GR                                              AS AMOUNT,
          CASES.USER_ID                                                     AS USER_ID,
          get_days_to_action(p_institution_number, CASES.ACQUIRER_REFERENCE, p_date, CASES.CENTRAL_DATE) AS DAYS_TO_ACTION,

          CASES.CLIENT_NUMBER                                               AS CLIENT_NUMBER,
          CASES.RETRIEVAL_REFERENCE                                         AS RETRIEVAL_REFERENCE,
          CASES.AUTH_CODE                                                   AS AUTH_CODE,
          CASES.ACQUIRER_REFERENCE                                          AS ACQUIRER_REFERENCE,
          CASES.VALUE_DATE                                                  AS VALUE_DATE,
          CASES.CARD_NUMBER                                                 AS CARD_NUMBER,
          CASES.CARD_ORGANIZATION                                           AS CARD_ORGANIZATION,
          CASES.MESSAGE_SUNDRY_TEXT                                         AS NOTE_TEXT,
          CASES.CHARGEBACK_REASON                                           AS CHARGEBACK_REASON,
          CASES.RETRIEVAL_REASON                                            AS RETRIEVAL_REASON,
          CASES.DISPUTE_CONDITION                                           AS DISPUTE_CONDITION,
          CASES.ESCALATED_FLAG                                              AS ESCALATED_FLAG,
          RJCT_MSG.REJECT_REASON || ' - ' ||  RJCT_MSG.REJECT_DESCRIPTION   AS MESSAGE_FROM_SCHEME,
          CASES.RAPID_DISPUTE_RESOLUTION                                    AS RAPID_DISPUTE_RESOLUTION
        FROM
          (get_all_cases(p_institution_number, p_current_language,
                        p_default_language, p_date, p_is_portal_request)) CASES,
          BWT_SUNDRY_TYPE SUN_TYPE,
          BWT_CASE_STATUS CASE_STATUS,
          BWT_ACTION_TAKEN ACT,
          BWT_CURRENCY CURR,
          BWT_REPROCESS_REJECTS RJCT_MSG

        WHERE SUN_TYPE.INSTITUTION_NUMBER = CASES.INSTITUTION_NUMBER
              AND SUN_TYPE.INDEX_FIELD = CASES.SUNDRY_TYPE
              AND SUN_TYPE.LANGUAGE = CASE
                                      WHEN EXISTS(
                                          SELECT INDEX_FIELD
                                          FROM BWT_SUNDRY_TYPE
                                          WHERE LANGUAGE = p_current_language
                                                AND INSTITUTION_NUMBER = SUN_TYPE.INSTITUTION_NUMBER
                                                AND INDEX_FIELD = SUN_TYPE.INDEX_FIELD)
                                        THEN p_current_language
                                      ELSE p_default_language
                                      END

              AND CASE_STATUS.INSTITUTION_NUMBER = CASES.INSTITUTION_NUMBER
              AND CASE_STATUS.INDEX_FIELD = CASES.CASE_STATUS
              AND CASE_STATUS.LANGUAGE = CASE
                                        WHEN EXISTS(
                                            SELECT INDEX_FIELD
                                            FROM BWT_CASE_STATUS
                                            WHERE LANGUAGE = p_current_language
                                                  AND INSTITUTION_NUMBER = CASE_STATUS.INSTITUTION_NUMBER
                                                  AND INDEX_FIELD = CASE_STATUS.INDEX_FIELD)
                                          THEN p_current_language
                                        ELSE p_default_language
                                        END

              AND ACT.INSTITUTION_NUMBER = CASES.INSTITUTION_NUMBER
              AND ACT.INDEX_FIELD = CASES.RULE_ACTION
              AND ACT.LANGUAGE = CASE
                                WHEN EXISTS(
                                    SELECT INDEX_FIELD
                                    FROM BWT_ACTION_TAKEN
                                    WHERE LANGUAGE = p_current_language
                                          AND INSTITUTION_NUMBER = ACT.INSTITUTION_NUMBER
                                          AND INDEX_FIELD = ACT.INDEX_FIELD)
                                  THEN p_current_language
                                ELSE p_default_language
                                END

              AND CURR.INSTITUTION_NUMBER = CASES.INSTITUTION_NUMBER
              AND CURR.ISO_CODE = CASES.TRAN_CURRENCY
              AND CURR.LANGUAGE = CASE
                                  WHEN EXISTS(
                                      SELECT ISO_CODE
                                      FROM BWT_CURRENCY
                                      WHERE LANGUAGE = p_current_language
                                            AND INSTITUTION_NUMBER = CURR.INSTITUTION_NUMBER
                                            AND ISO_CODE = CURR.ISO_CODE)
                                    THEN p_current_language
                                  ELSE p_default_language
                                  END
              AND RJCT_MSG.INSTITUTION_NUMBER(+) = CASES.INSTITUTION_NUMBER
              AND RJCT_MSG.REJECT_REASON(+) = CASES.INDEX_MESSAGE_FROM_SCHEME;
      BEGIN
        FOR cursor_record IN tbl_cursor LOOP
          PIPE ROW (cursor_record);
        END LOOP;
      END;

  FUNCTION get_operator_actions(p_institution_number VARCHAR2, p_card_org VARCHAR2, p_sundry_type VARCHAR2,
                                p_previous_action    VARCHAR2, p_chargeback_reason VARCHAR2,
                                p_current_language VARCHAR2, p_escalated_flag VARCHAR2,
                                p_case_number INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
                                p_default_language   VARCHAR2 DEFAULT 'USA', p_case_status VARCHAR2 DEFAULT NULL)
    RETURN TBL_ACTION_TAKEN
    PIPELINED AS
      CURSOR tbl_cursor IS
        SELECT *
        FROM TABLE (get_actions(p_institution_number, p_card_org, c_operator_actor, p_sundry_type, p_case_number, p_escalated_flag, p_previous_action,
                                p_chargeback_reason, p_current_language, p_default_language, p_case_status));
      BEGIN
        FOR cursor_record IN tbl_cursor LOOP
          PIPE ROW (cursor_record);
        END LOOP;
      END;

  FUNCTION get_support_actions(p_institution_number VARCHAR2, p_card_org VARCHAR2, p_sundry_type VARCHAR2,
                                p_previous_action    VARCHAR2, p_chargeback_reason VARCHAR2,
                                p_current_language VARCHAR2, p_escalated_flag VARCHAR2,
                                p_case_number INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
                                p_default_language   VARCHAR2 DEFAULT 'USA', p_case_status VARCHAR2 DEFAULT NULL)
    RETURN TBL_ACTION_TAKEN
    PIPELINED AS
      CURSOR tbl_cursor IS
        SELECT *
        FROM TABLE (get_actions(p_institution_number, p_card_org, c_support_actor, p_sundry_type, p_case_number, p_escalated_flag, p_previous_action,
                                p_chargeback_reason, p_current_language, p_default_language, p_case_status));
      BEGIN
        FOR cursor_record IN tbl_cursor LOOP
          PIPE ROW (cursor_record);
        END LOOP;
      END;

  FUNCTION get_merchant_actions(p_institution_number VARCHAR2,
                                p_card_org VARCHAR2,
                                p_sundry_type VARCHAR2,
                                p_chargeback_reason VARCHAR2,
                                p_current_language  VARCHAR2,
                                p_escalated_flag VARCHAR2,
                                p_case_number INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
                                p_previous_action SYS_SUNDRY_ACTION_SETUP.PREVIOUS_ACTION%TYPE,
                                p_default_language VARCHAR2 DEFAULT 'USA',
                                p_case_status VARCHAR2 DEFAULT NULL)
    RETURN TBL_ACTION_TAKEN
    PIPELINED AS
      CURSOR tbl_cursor IS
        SELECT *
        FROM TABLE (get_actions(p_institution_number,
                                p_card_org,
                                c_merchant_actor,
                                p_sundry_type,
                                p_case_number,
                                p_escalated_flag,
                                p_previous_action,
                                p_chargeback_reason,
                                p_current_language,
                                p_default_language,
                                p_case_status ));
      BEGIN
        FOR cursor_record IN tbl_cursor LOOP
          PIPE ROW (cursor_record);
        END LOOP;
      END;

  FUNCTION get_actions(p_institution_number VARCHAR2,
                        p_card_org VARCHAR2,
                        p_actor VARCHAR2,
                        p_sundry_type VARCHAR2,
                        p_case_number INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
                        p_escalated_flag VARCHAR2,
                        p_previous_action VARCHAR2 DEFAULT NULL,
                        p_chargeback_reason VARCHAR2 DEFAULT NULL,
                        p_current_language VARCHAR2 DEFAULT NULL,
                        p_default_language VARCHAR2 DEFAULT NULL,
                        p_case_status VARCHAR2 DEFAULT NULL)
    RETURN TBL_ACTION_TAKEN
    PIPELINED AS
      pre_dispute_case SYS_SUNDRY_ACTION_SETUP.PRE_DISPUTE_CASE%TYPE;

      CURSOR tbl_cursor (cp_pre_dispute_case varchar2) 
      IS
        SELECT
          SETUP.ACTION  AS ACTION_ID,
          ACT.NARRATIVE AS ACTION_NARRATIVE,
          SETUP.NEW_SUNDRY_TYPE,
          SETUP.NEW_CASE_STATUS,
          SETUP.EVIDENCE_REQUIRED,
          SETUP.FEE_CONFIRMATION,
          SETUP.NOTES_REQUIRED,
          SETUP.CHARGEBACK_REASON_CODE,
          get_document_rename_mask(p_institution_number, p_card_org, ACT.INDEX_FIELD) AS DOCUMENT_RENAME_MASK,
          SETUP.OUTWARD_DOCUMENTATION AS OUTWARD_DOCUMENTATION,
          SETUP.NEW_ESCALATED_FLAG,
          SETUP.REVERT_ACTION,
          SETUP.ACTION_TO_REVERT,
          SETUP.CENTRAL_DATE_REQUIRED,
          SETUP.REQUIRES_DATE,
          SETUP.RISK_ACCEPTANCE_REQUIRED
        FROM SYS_SUNDRY_ACTION_SETUP SETUP,
        BWT_ACTION_TAKEN ACT
        WHERE SETUP.rowid =
            get_highest_priority_rule(p_institution_number, p_card_org, p_chargeback_reason, p_previous_action,
                                      p_sundry_type, p_case_status, p_escalated_flag, SETUP.ACTION, SETUP.ACTOR, cp_pre_dispute_case)
            -- all 6 columns can be either specific or generic. The function call will return the highest priority combination.
              AND SETUP.ACTOR = p_actor

              AND ACT.INSTITUTION_NUMBER = p_institution_number
              AND ACT.INDEX_FIELD = SETUP.ACTION
              AND ACT.LANGUAGE = CASE
                                WHEN EXISTS(
                                    SELECT INDEX_FIELD
                                    FROM BWT_ACTION_TAKEN
                                    WHERE LANGUAGE = p_current_language
                                          AND INDEX_FIELD = ACT.INDEX_FIELD
                                          AND INSTITUTION_NUMBER = ACT.INSTITUTION_NUMBER)
                                  THEN p_current_language
                                ELSE p_default_language
                                END;
      BEGIN
        IF p_card_org = c_visa_card_org THEN 
          pre_dispute_case := is_rapid_dispute_resolution_case(p_case_number, p_institution_number);
        ELSE 
          pre_dispute_case := c_confirmation_no;
        END IF;

        FOR cursor_record IN tbl_cursor(pre_dispute_case) LOOP
          PIPE ROW (cursor_record);
        END LOOP;
      END;

  FUNCTION fetch_merchant_dispute_statistics(p_institution_number INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
                                              p_date VARCHAR2 DEFAULT TO_CHAR(SYSDATE, 'YYYYMMDD'),
                                              p_client_numbers BW_DISPUTES_ARRAY DEFAULT NULL,
                                              p_group_number INT_SUNDRY_HISTORY.GROUP_NUMBER%TYPE DEFAULT NULL)
    RETURN TBL_MERCHANT_DISPUTE_STATISTICS
    PIPELINED AS
      v_client_filter_count NUMBER := 0;
    BEGIN
      -- Get the quantity of client numbers to filter by.
      SELECT COUNT(*)
      INTO v_client_filter_count
      FROM TABLE (p_client_numbers);

      FOR CURSOR_RECORD
      IN (
        WITH HEADER AS (
          SELECT /*+materialize*/ *
          FROM INT_SUNDRY_HISTORY HEADER
          WHERE HEADER.INSTITUTION_NUMBER = p_institution_number
          AND HEADER.RULE_ACTION = c_case_header_action -- '110'
          AND v_client_filter_count = 0

          -- cater for optional group_number parameter
          AND (p_group_number IS NULL
              OR HEADER.GROUP_NUMBER = p_group_number)
          UNION
          SELECT /*+materialize*/ *
          FROM INT_SUNDRY_HISTORY HEADER
          WHERE HEADER.INSTITUTION_NUMBER = p_institution_number
          AND HEADER.RULE_ACTION = c_case_header_action -- '110'
          AND v_client_filter_count > 0
          AND HEADER.CLIENT_NUMBER IN (SELECT COLUMN_VALUE FROM TABLE(p_client_numbers))
          
          -- cater for optional group_number parameter
          AND (p_group_number IS NULL
              OR HEADER.GROUP_NUMBER = p_group_number)
        ),
        CASES AS (
          SELECT  /*+cardinality (HEADER, 10) leading (HEADER, LAST_REC, HIST_LAST_CENTRAL, LAST_CENTRAL_DATE) */
                    HEADER.INSTITUTION_NUMBER,
                    HEADER.CASE_NUMBER,
                    HEADER.CLIENT_NUMBER,
                    HEADER.RECORD_DATE AS CASE_CREATION_DATE,
                    LAST_REC.SUNDRY_TYPE,
                    HEADER.CASE_STATUS,
                    get_days_to_action(HEADER.INSTITUTION_NUMBER,
                                        HEADER.ACQUIRER_REFERENCE,
                                        p_date,
                                        LAST_CENTRAL_DATE.CENTRAL_DATE) AS DAYS_TO_ACTION

          FROM
              HEADER,
              INT_SUNDRY_HISTORY LAST_REC,--  last record for case
              INT_SUNDRY_HISTORY LAST_CENTRAL_DATE -- last record which has a central_date

          WHERE LAST_REC.INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
          AND LAST_REC.SUNDRY_HISTORY_ID = get_max_history_id(HEADER.INSTITUTION_NUMBER, HEADER.ACQUIRER_REFERENCE)

          AND LAST_CENTRAL_DATE.INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
          AND LAST_CENTRAL_DATE.SUNDRY_HISTORY_ID = (SELECT MAX(SUNDRY_HISTORY_ID)
                                                      FROM INT_SUNDRY_HISTORY HIST_LAST_CENTRAL
                                                      WHERE INSTITUTION_NUMBER = HEADER.INSTITUTION_NUMBER
                                                      AND ACQUIRER_REFERENCE = HEADER.ACQUIRER_REFERENCE
                                                      AND CENTRAL_DATE IS NOT NULL)
          )
        --COUNTS BY CASE STATUS
        SELECT
          CASES.CASE_STATUS AS INDEX_FIELD,
          COUNT(CASES.CASE_STATUS)
        FROM CASES
        GROUP BY CASES.CASE_STATUS
        UNION
        --OPENED TODAY
        SELECT
          c_opened_today AS INDEX_FIELD,
          COUNT(CASES.CASE_NUMBER)
        FROM CASES
        WHERE CASES.CASE_CREATION_DATE = p_date
        UNION
        --IMMEDIATE ACTION
        SELECT
          c_immediate_action AS INDEX_FIELD,
          COUNT(CASES.CASE_NUMBER)
        FROM CASES
        WHERE CASES.CASE_STATUS = c_open_case_status --'001'
        AND CASES.DAYS_TO_ACTION <= (SELECT CONFIG_VALUE
                      FROM (SELECT CONF.CONFIG_VALUE, ROW_NUMBER() OVER (ORDER BY INSTITUTION_NUMBER DESC) RN
                          FROM SYS_CONFIGURATION CONF
                          WHERE CONFIG_SECTION = 'Web-GUI'
                          AND CONFIG_KEYWORD = 'final-merchant-alert')
                      WHERE RN = 1)
        UNION
        --NEXT ATTENTION
        SELECT
          c_next_attention AS INDEX_FIELD,
          COUNT(CASES.CASE_NUMBER)
        FROM CASES
        WHERE CASES.CASE_STATUS = c_open_case_status --'001'
        AND CASES.DAYS_TO_ACTION <= (SELECT CONFIG_VALUE
                      FROM (SELECT CONF.CONFIG_VALUE, ROW_NUMBER() OVER (ORDER BY INSTITUTION_NUMBER DESC) RN
                          FROM SYS_CONFIGURATION CONF
                          WHERE CONFIG_SECTION = 'Web-GUI'
                          AND CONFIG_KEYWORD = 'first-merchant-alert')
                      WHERE RN = 1)
        AND CASES.DAYS_TO_ACTION > (SELECT CONFIG_VALUE
                      FROM (SELECT CONF.CONFIG_VALUE, ROW_NUMBER() OVER (ORDER BY INSTITUTION_NUMBER DESC) RN
                          FROM SYS_CONFIGURATION CONF
                          WHERE CONFIG_SECTION = 'Web-GUI'
                          AND CONFIG_KEYWORD = 'final-merchant-alert')
                      WHERE RN = 1)
        )
      LOOP
        PIPE ROW (CURSOR_RECORD);
      END LOOP;
    END;

  /**
  * Returns the rename mask which best matches the parameters it receieves and replaces
  * the institution and card organization short name placeholders if any are found.
  *
  * @param p_institution_number   The institution numer which the case is for.
  * @param p_card_organization    The card organization index field value matching the case that is being actioned.
  * @param p_action_id            The action being taken's index field value.
  *
  * @return v_document_rename_mask  The rename mask with institution and card organization place holders already replaced.
  */
  FUNCTION get_document_rename_mask(p_institution_number VARCHAR2, p_card_organization VARCHAR2, p_action_id VARCHAR2)
    RETURN VARCHAR2
    AS
      v_document_rename_mask VARCHAR2(100);
    BEGIN
      SELECT  REPLACE(
                REPLACE(
                  get_highest_priority_document_rename_mask(
                                                p_institution_number,
                                                p_card_organization,
                                                (SELECT RENAME_MASK_ID
                                                FROM BWT_ACTION_TAKEN
                                                WHERE INSTITUTION_NUMBER = p_institution_number
                                                AND INDEX_FIELD = p_action_id
                                                AND LANGUAGE = 'USA')),
                  '<institution_name>',
                  NVL(SIL.INSTITUTION_SHORT_NAME, '<missing_institution_setup>')),
                '<card_organization>',
                NVL(BCO.CARD_ORG_SHORT_NAME, '<missing_card_org_setup>'))
      INTO v_document_rename_mask
      FROM SYS_INSTITUTION_LICENCE SIL,
          BWT_CARD_ORGANIZATION BCO
      WHERE  BCO.INSTITUTION_NUMBER = p_institution_number
      AND BCO.INDEX_FIELD = p_card_organization
      AND SIL.INSTITUTION_NUMBER = BCO.INSTITUTION_NUMBER;

      RETURN v_document_rename_mask;
    END;

  /**
  * Returns the rename mask which best matches the parameters it receieves.
  *
  * @param p_institution_number   The institution numer which the case is for.
  * @param p_card_organization    The card organization index field value matching the case that is being actioned.
  * @param p_rename_mask_id       The rename mask ID number.
  *
  * @return v_document_rename_mask  The rename mask which best matches the parameters received.
  */
  FUNCTION get_highest_priority_document_rename_mask(p_institution_number VARCHAR2, p_card_organization VARCHAR2,
                                                      p_rename_mask_id VARCHAR2)
    RETURN SYS_SUNDRY_MASK_RENAME.RENAME_MASK%TYPE
    AS
      v_document_rename_mask SYS_SUNDRY_MASK_RENAME.RENAME_MASK%TYPE;
    BEGIN
      SELECT RENAME_MASK INTO v_document_rename_mask
      FROM
          ( SELECT
              ROW_NUMBER()
              OVER (
                ORDER BY INSTITUTION_NUMBER DESC, CARD_ORGANIZATION
              ) AS RN,
          INSTITUTION_NUMBER,
          CARD_ORGANIZATION,
          RENAME_MASK
          FROM SYS_SUNDRY_MASK_RENAME
          WHERE  INSTITUTION_NUMBER IN (p_institution_number, c_generic_institution)
              AND CARD_ORGANIZATION IN (p_card_organization, BWTPAD('BWT_CARD_ORGANIZATION', c_generic_card_org))
              AND ID = p_rename_mask_id
            ) WHERE RN = 1;

      RETURN v_document_rename_mask;
    END;

  FUNCTION get_open_case(p_institution_number VARCHAR2, p_transaction_slip VARCHAR2)
    RETURN NUMERIC
    AS
      v_column_count NUMERIC;
    BEGIN
      BEGIN
        SELECT COUNT(*) AS COLUMNS_EXIST
          INTO v_column_count
        FROM INT_TRANSACTIONS TRANS, INT_SUNDRY_HISTORY SUNDRY_HISTORY
        WHERE TRANS.INSTITUTION_NUMBER = p_institution_number
              AND TRANS.TRANSACTION_SLIP = p_transaction_slip
              AND TRANS.TRANSACTION_CATEGORY = BWTPAD('BWT_TRANSACTION_CATEGORY', '001')
              AND TRANS.INSTITUTION_NUMBER = SUNDRY_HISTORY.INSTITUTION_NUMBER
              AND TRANS.TRANSACTION_SLIP = SUNDRY_HISTORY.NUMBER_ORIGINAL_SLIP
              AND SUNDRY_HISTORY.CASE_STATUS = BWTPAD('BWT_CASE_STATUS', '001');

      EXCEPTION WHEN NO_DATA_FOUND THEN
        v_column_count := 0;
      END;

      RETURN v_column_count;
    END;

  FUNCTION get_case_history(p_institution_number VARCHAR2 DEFAULT NULL,
                            p_case_number VARCHAR2 DEFAULT NULL,
                            p_rule_action VARCHAR2 DEFAULT NULL,
                            p_current_language   VARCHAR2 DEFAULT 'USA',
                            p_default_language   VARCHAR2 DEFAULT 'USA',
                            p_is_portal_request  VARCHAR2 DEFAULT 'FALSE')
    RETURN TBL_CASE_HISTORY
    PIPELINED AS
      CURSOR tbl_cursor IS
        SELECT SUNDRY_HISTORY_ID,
               INSTITUTION_NUMBER,
               CASE_NUMBER,
               SUNDRY_TYPE,
               SUNDRY_TYPE_DESCRIPTION,
               CASE_STATUS,
               RULE_ACTION,
               RECORD_DATE,
               RECORD_TIME,
               DOCUMENT_NAME,
               DESCRIPTIVE_DOC_NAME,
               NOTE_TEXT,
               CLIENT_NUMBER,
               EXTERNAL_CASE_NUMBER,
               SUNDRY_STATUS,
               USER_ID,
               OWNER_ID,
               FILE_EXTENSION_ID,
               FILE_EXTENSION,
               CENTRAL_DATE,
               MP_USER_ID,
               TIME_FRAME,
               REVERSAL_FLAG,
               CASE_GROUP,
               SUNDRY_TRANSACTION_SLIP,
               RULE_ID,
               DATE_TIME,
               MERCHANT_VISIBLE,
               DOCUMENT_LOCATION,
               SUNDRY_REASON_DESC,
               MESSAGE_FROM_SCHEME,
               LANGUAGE,
               CARD_ORGANIZATION,
               USERNAME,
               RISK_ACCEPTED,
               PLANNED_REFUND_DATE
        FROM VIEW_DISPUTE_HIST_LIST HIS
        WHERE
              -- optional institution number
              (p_institution_number IS NULL OR HIS.INSTITUTION_NUMBER = p_institution_number)
          AND (p_case_number IS NULL OR HIS.CASE_NUMBER = p_case_number)
          -- optional rule action parameter
          AND (p_rule_action IS NULL OR HIS.RULE_ACTION = p_rule_action)
          AND (p_rule_action IS NOT NULL
              -- if rule action not provided, set default filters
               OR (NOT (HIS.RULE_ACTION = c_miscellaneous_action --'104'
                    AND HIS.CARD_ORGANIZATION = c_discover_card_org --'023
                    AND HIS.SUNDRY_TYPE = c_msg_confirmation_sundry_type) --'065'

                    AND ((p_is_portal_request = 'FALSE') -- Not a portal request
                        OR
                            (NOT(HIS.RULE_ACTION = c_miscellaneous_action --'104'
                              AND HIS.CARD_ORGANIZATION = c_discover_card_org --'023'
                              AND HIS.SUNDRY_TYPE = c_rejects) --'013'
                            AND
                              NOT(HIS.RULE_ACTION = c_confirmation_rejected_handled_action) --'247'
                            )
                        )
                    )
                );
      BEGIN
        FOR cursor_record IN tbl_cursor LOOP
          PIPE ROW (cursor_record);
        END LOOP;
      END;

  FUNCTION get_merchant_case_history(p_institution_number VARCHAR2, p_case_number VARCHAR2,
                                       p_current_language   VARCHAR2 DEFAULT 'USA',
                                       p_default_language   VARCHAR2 DEFAULT 'USA')
    RETURN TBL_MERCHANT_CASE_HISTORY
    PIPELINED AS
      v_parent_history_id INT_SUNDRY_HISTORY.SUNDRY_HISTORY_ID%TYPE;
      CURSOR tbl_cursor IS
        WITH HISTORY AS(
            SELECT /*+MATERIALIZE*/ *
            FROM TABLE (GET_CASE_HISTORY(P_INSTITUTION_NUMBER => p_institution_number,
                                        P_CASE_NUMBER => p_case_number,
                                        P_CURRENT_LANGUAGE => p_current_language,
                                        P_DEFAULT_LANGUAGE => p_default_language,
                                        P_IS_PORTAL_REQUEST => 'TRUE'))  HISTORY
            WHERE HISTORY.RULE_ACTION NOT IN (SELECT * FROM TABLE(GET_EXCLUDE_ACTIONS(c_non_merchant_viewable)))
            AND NVL(HISTORY.MERCHANT_VISIBLE, c_confirmation_yes) != c_confirmation_no)

        SELECT '' AS PARENT_SUNDRY_HISTORY_ID,
               c_confirmation_no AS DOCUMENT, -- Temporary Value
                ACTIONS.*
        FROM HISTORY ACTIONS
        WHERE ACTIONS.RULE_ACTION <> c_attach_document_action
        UNION ALL
        SELECT (SELECT MIN(SUNDRY_HISTORY_ID)
                FROM HISTORY
                WHERE SUNDRY_HISTORY_ID > ATTACHMENTS.SUNDRY_HISTORY_ID
                AND RULE_ACTION <> c_attach_document_action) AS PARENT_SUNDRY_HISTORY_ID,
                c_confirmation_no AS DOCUMENT, -- Temporary Value
                ATTACHMENTS.*
        FROM HISTORY ATTACHMENTS
        WHERE ATTACHMENTS.RULE_ACTION = c_attach_document_action
        ORDER BY 3;
      v_modified_row tbl_cursor%ROWTYPE;
    BEGIN
      FOR cursor_record IN tbl_cursor LOOP
        v_modified_row := cursor_record;
        IF v_parent_history_id IS NOT NULL AND v_parent_history_id = cursor_record.SUNDRY_HISTORY_ID THEN
          v_modified_row.DOCUMENT := c_confirmation_yes;
        ELSE
          v_modified_row.DOCUMENT := c_confirmation_no;
        END IF;
        PIPE ROW (v_modified_row);
        v_parent_history_id := cursor_record.PARENT_SUNDRY_HISTORY_ID;
      END LOOP;
    END;

  /**
  * Returns a pipeline with the case header record details
  *
  * @param p_institution_number   The institution numer which the case is for
  * @param p_case_number          The case number to get the header for
  *
  * @return TBL_CASE_HEADER       A table object containing the case header details
  */
  FUNCTION get_case_header_details(p_institution_number VARCHAR2, p_case_number VARCHAR2)
    RETURN TBL_CASE_HEADER
    PIPELINED AS
      CURSOR tbl_cursor IS
                SELECT  SUNDRY_HISTORY_ID,
                        NUMBER_ORIGINAL_SLIP,
                        SUNDRY_STATUS,
                        RECORD_DATE,
                        ACQUIRER_REFERENCE,
                        CENTRAL_DATE,
                        CARD_ORGANIZATION,
                        EXTERNAL_CASE_NUMBER,
                        CASE_STATUS,
                        ESCALATED_FLAG,
                        CLIENT_NUMBER,
                        USER_ID,
                        OWNER_ID,
                        SENT_FLAG
                FROM INT_SUNDRY_HISTORY
                WHERE INSTITUTION_NUMBER = p_institution_number
                AND CASE_NUMBER = p_case_number
                AND RULE_ACTION = c_case_header_action;
    BEGIN
      FOR cursor_record IN tbl_cursor LOOP
        PIPE ROW (cursor_record);
      END LOOP;
    END;

  /**
  * Returns the last chargeback reason of the given case where the sundry type
  * was last a chargeback. For Discover this also applied for pre-arbitration
  * and arbitration, as chargeback reason codes are used for these.
  *
  * @param p_institution_number   The institution numer which the case is for.
  * @param p_case_number          The case number to get the info for.
  * @param p_acquirer_reference   The acquirer reference number to get the info for.
  *
  * @return v_chargeback_reason   The index field value for the chargeback reason
  */
  FUNCTION get_chargeback_reason (p_institution_number VARCHAR2, p_acquirer_reference VARCHAR2)
    RETURN INT_SUNDRY_HISTORY.CHARGEBACK_REASON%TYPE
    AS
      v_chargeback_reason INT_SUNDRY_HISTORY.CHARGEBACK_REASON%TYPE;
    BEGIN
      SELECT REASON.CHARGEBACK_REASON
      INTO v_chargeback_reason
      FROM INT_SUNDRY_HISTORY REASON
      WHERE REASON.INSTITUTION_NUMBER = p_institution_number
      AND REASON.SUNDRY_HISTORY_ID = (SELECT MAX(LAST.SUNDRY_HISTORY_ID)
                                      FROM INT_SUNDRY_HISTORY LAST
                                      WHERE LAST.INSTITUTION_NUMBER = p_institution_number
                                      AND LAST.ACQUIRER_REFERENCE = p_acquirer_reference
                                      AND (LAST.SUNDRY_TYPE IN (c_first_chargeback,  --'001'
                                                          c_second_chargeback) --'002'
                                          -- Discover use chargeback reason codes for pre-arb and arb
                                          OR (LAST.CARD_ORGANIZATION = c_discover_card_org --'023'
                                              AND LAST.SUNDRY_TYPE IN (c_pre_arbitration,  --'095'
                                                                      c_arbitration)      --'064'
                                              )
                                          )
                                      AND CHARGEBACK_REASON IS NOT NULL);

      RETURN v_chargeback_reason;
    END;

  /**
  * Returns the last dispute condition of the given case.
  *
  * @param p_institution_number   The institution numer which the case is for.
  * @param p_case_number          The case number to get the info for.
  * @param p_acquirer_reference   The acquirer reference number to get the info for.
  *
  * @return v_dispute_condition   The index field value fo the dispute condition
  */
  FUNCTION get_dispute_condition (p_institution_number VARCHAR2, p_acquirer_reference VARCHAR2)
    RETURN INT_SUNDRY_TRANSACTIONS.DISPUTE_CONDITION%TYPE
    AS
      v_dispute_condition INT_SUNDRY_TRANSACTIONS.DISPUTE_CONDITION%TYPE;
    BEGIN
      SELECT ST.DISPUTE_CONDITION
      INTO v_dispute_condition
      FROM INT_SUNDRY_TRANSACTIONS ST
      WHERE ST.INSTITUTION_NUMBER = p_institution_number
      AND ST.SUNDRY_TYPE NOT IN (c_reversal, c_manual_chargeback, c_transfer, c_chargeback_transfer) -- '009', '079', '012', '040'
      AND ST.SUNDRY_TRANSACTION_SLIP = (SELECT MAX(SH.SUNDRY_TRANSACTION_SLIP)
                                        FROM INT_SUNDRY_HISTORY SH
                                        WHERE SH.INSTITUTION_NUMBER = p_institution_number
                                        AND SH.ACQUIRER_REFERENCE = p_acquirer_reference);

      RETURN v_dispute_condition;
    END;

  /**
  * Returns the last retrieval reason of the given case, unless the case already
  * has a chargeback. In Discover's case, the same exception applies if the case
  * has a pre-arbitrartion or arbitration.
  *
  * @param p_institution_number   The institution numer which the case is for.
  * @param p_case_number          The case number to get the info for.
  * @param p_acquirer_reference   The acquirer reference number to get the info for.
  *
  * @return v_retrieval_reason   The index field value fo the retrieval reason
  */
  FUNCTION get_retrieval_reason (p_institution_number VARCHAR2, p_acquirer_reference VARCHAR2)
    RETURN INT_SUNDRY_HISTORY.RETRIEVAL_REASON%TYPE
    AS
      v_retrieval_reason INT_SUNDRY_HISTORY.RETRIEVAL_REASON%TYPE;
    BEGIN
      SELECT REASON.RETRIEVAL_REASON
      INTO v_retrieval_reason
      FROM INT_SUNDRY_HISTORY REASON
      WHERE REASON.INSTITUTION_NUMBER = p_institution_number
      AND REASON.SUNDRY_HISTORY_ID = (SELECT MAX(LAST.SUNDRY_HISTORY_ID)
                                    FROM INT_SUNDRY_HISTORY LAST
                                    WHERE LAST.INSTITUTION_NUMBER = p_institution_number
                                    AND LAST.ACQUIRER_REFERENCE = p_acquirer_reference
                                    AND LAST.RETRIEVAL_REASON IS NOT NULL
                                    AND NOT EXISTS (SELECT EXCLUDE.SUNDRY_HISTORY_ID
                                                    FROM INT_SUNDRY_HISTORY EXCLUDE
                                                    WHERE EXCLUDE.INSTITUTION_NUMBER = LAST.INSTITUTION_NUMBER
                                                    AND EXCLUDE.ACQUIRER_REFERENCE = LAST.ACQUIRER_REFERENCE
                                                    AND (EXCLUDE.SUNDRY_TYPE IN ( c_first_chargeback,   --'001'
                                                                                  c_second_chargeback)  --'002'
                                                          -- Discover use chargeback reason codes for pre-arb and arb
                                                          OR (EXCLUDE.CARD_ORGANIZATION = c_discover_card_org --'023'
                                                              AND EXCLUDE.SUNDRY_TYPE IN (c_pre_arbitration,  --'095'
                                                                                          c_arbitration)      --'064'
                                                              )
                                                          )
                                                  )
                                );

      RETURN v_retrieval_reason;
    END;

  FUNCTION get_reason_lookup( p_institution_number VARCHAR2, p_chargeback_reason VARCHAR2,
                              p_dispute_condition VARCHAR2, p_retrieval_reason VARCHAR2,
                              p_card_organization VARCHAR2)
  RETURN VARCHAR2
  AS
    v_reason_lookup               VARCHAR2(30);
    v_is_pin_debit                BOOLEAN := TRUE;
    v_pin_debit_card_organization CBR_CHANNEL_DEFINITION.CARD_ORGANIZATION%TYPE;
  BEGIN
    BEGIN
      SELECT DISTINCT CARD_ORGANIZATION
        INTO v_pin_debit_card_organization
      FROM CBR_CHANNEL_DEFINITION DEF
      WHERE DEF.INSTITUTION_NUMBER = p_institution_number
        AND TRANSACTION_DESTINATION IN (SELECT TRANSACTION_DESTINATION
                                      FROM SYS_SUNDRY_PIN_DEBIT_CHANNEL
                                      WHERE INSTITUTION_NUMBER = DEF.INSTITUTION_NUMBER)
        AND DEF.CARD_ORGANIZATION = p_card_organization;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      v_is_pin_debit := FALSE;
    END;

    IF p_retrieval_reason IS NOT NULL THEN
      v_reason_lookup := c_generic_retrieval_reason_lookup;
    ELSIF p_dispute_condition IS NOT NULL THEN
      v_reason_lookup := c_generic_chargeback_reason_lookup;
    ELSIF p_chargeback_reason IS NOT NULL AND NOT v_is_pin_debit THEN
      v_reason_lookup := c_generic_chargeback_reason_lookup;
    ELSIF p_chargeback_reason IS NOT NULL AND v_is_pin_debit THEN
      v_reason_lookup := c_old_chargeback_reason_lookup;
    END IF;

  RETURN v_reason_lookup;
  END;

  FUNCTION get_reason_description(p_institution_number VARCHAR2, p_chargeback_reason VARCHAR2,
                                  p_dispute_condition VARCHAR2, p_retrieval_reason VARCHAR2,
                                  p_current_language VARCHAR2, p_card_organization VARCHAR2,
                                  p_default_language VARCHAR2 DEFAULT 'USA')
  RETURN VARCHAR2
  AS
  v_reason_lookup VARCHAR2(30);
  v_reason_description VARCHAR2(70);
  v_generic_chargeback_reason BWT_VCR_DISPUTE_CONDITION.INDEX_FIELD%TYPE;
  v_reason_index_field BWT_CHARGEBACK_REASON.INDEX_FIELD%TYPE;
  BEGIN
    BEGIN
      v_reason_lookup := get_reason_lookup( p_institution_number, p_chargeback_reason,
                                            p_dispute_condition, p_retrieval_reason,
                                            p_card_organization);

      IF v_reason_lookup = c_old_chargeback_reason_lookup THEN
        SELECT VISA_REASON_CODE || ' - ' || SUNDRY_REASON AS REASON_CODE_DESCRIPTION
          INTO v_reason_description
        FROM BWT_CHARGEBACK_REASON CHG
        WHERE CHG.INSTITUTION_NUMBER = p_institution_number
          AND CHG.INDEX_FIELD = p_chargeback_reason
          AND CHG.LANGUAGE = CASE
                              WHEN EXISTS(
                                    SELECT INDEX_FIELD
                                    FROM BWT_CHARGEBACK_REASON
                                    WHERE LANGUAGE = p_current_language
                                    AND INSTITUTION_NUMBER = CHG.INSTITUTION_NUMBER
                                    AND INDEX_FIELD = CHG.INDEX_FIELD)
                              THEN p_current_language
                              ELSE p_default_language
                            END;

      ELSIF v_reason_lookup = c_generic_chargeback_reason_lookup THEN
        IF p_card_organization = BWTPAD('BWT_CARD_ORGANIZATION', '003') THEN
          v_reason_index_field := nvl(p_dispute_condition, p_chargeback_reason);
        ELSE
          v_reason_index_field := p_chargeback_reason;
        END IF;

        SELECT REASON_CODE_DESCRIPTION
          INTO v_reason_description
        FROM BWT_GENERIC_CHARGEBACK_REASON CHG
        WHERE CHG.INSTITUTION_NUMBER = p_institution_number
          AND CHG.INDEX_FIELD = v_reason_index_field
          AND CHG.LANGUAGE = CASE
                              WHEN EXISTS(
                                    SELECT INDEX_FIELD
                                    FROM BWT_GENERIC_CHARGEBACK_REASON
                                    WHERE LANGUAGE = p_current_language
                                    AND INSTITUTION_NUMBER = CHG.INSTITUTION_NUMBER
                                    AND INDEX_FIELD = CHG.INDEX_FIELD)
                              THEN p_current_language
                              ELSE p_default_language
                            END
          AND CHG.CARD_ORGANIZATION = p_card_organization;

      ELSIF v_reason_lookup = c_generic_retrieval_reason_lookup THEN
        SELECT REASON_CODE_DESCRIPTION
          INTO v_reason_description
        FROM BWT_GENERIC_RETRIEVAL_REASON RET
        WHERE RET.INSTITUTION_NUMBER = p_institution_number
          AND RET.INDEX_FIELD = p_retrieval_reason
          AND RET.LANGUAGE = CASE
                              WHEN EXISTS(
                                    SELECT INDEX_FIELD
                                    FROM BWT_GENERIC_RETRIEVAL_REASON
                                    WHERE LANGUAGE = p_current_language
                                    AND INSTITUTION_NUMBER = RET.INSTITUTION_NUMBER
                                    AND INDEX_FIELD = RET.INDEX_FIELD)
                              THEN p_current_language
                              ELSE p_default_language
                            END
          AND RET.CARD_ORGANIZATION = p_card_organization;

      END IF;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    v_reason_description := NULL;
  END;

  RETURN v_reason_description;
  END;

  --This function will return the highest priority rule for the received parameters.
  --Order of priority: INSTITUTION_NUMBER, CARD_ORGANIZATION, CHARGEBACK_REASON_CODE, PREVIOUS_ACTION, SUNDRY_TYPE, CASE_STATUS
  FUNCTION get_highest_priority_rule(p_institution_number VARCHAR2, p_card_organization VARCHAR2,   p_chargeback_reason VARCHAR2,
                     p_previous_action VARCHAR2,  p_sundry_type VARCHAR2, p_case_status VARCHAR2, p_escalated_flag VARCHAR2,  p_action VARCHAR2,
                     p_actor VARCHAR2, p_pre_dispute_case VARCHAR2)
  RETURN VARCHAR
  AS
  v_row_id VARCHAR2(20);
  BEGIN
    BEGIN
    SELECT rowid
    INTO v_row_id
    FROM(
      SELECT  INSTITUTION_NUMBER,
          CARD_ORGANIZATION,
          CHARGEBACK_REASON_CODE,
          PREVIOUS_ACTION,
          SUNDRY_TYPE,
          CASE_STATUS,
          ROW_NUMBER() OVER (ORDER BY INSTITUTION_NUMBER DESC, CARD_ORGANIZATION, CHARGEBACK_REASON_CODE, PREVIOUS_ACTION, SUNDRY_TYPE, CASE_STATUS, ESCALATED_FLAG, PRE_DISPUTE_CASE) AS PRIORITY
      FROM SYS_SUNDRY_ACTION_SETUP
      WHERE   INSTITUTION_NUMBER IN (p_institution_number, c_generic_institution)
        AND CARD_ORGANIZATION IN (p_card_organization, c_generic_card_org)
        AND CHARGEBACK_REASON_CODE IN (p_chargeback_reason,c_generic_chargeback_reason)
        AND PREVIOUS_ACTION IN (p_previous_action,c_generic_prev_action)
        AND SUNDRY_TYPE IN (p_sundry_type,c_generic_sundry_type)
        AND CASE_STATUS IN (p_case_status,c_generic_case_status)
        AND ESCALATED_FLAG IN (p_escalated_flag, c_generic_escalated_flag)
        AND PRE_DISPUTE_CASE IN (p_pre_dispute_case, c_generic_pre_dispute_case)
        AND ACTION = p_action
        AND ACTOR = p_actor
    )
    WHERE PRIORITY = 1;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    v_row_id := NULL;
  END;

    RETURN v_row_id;
  END;

  FUNCTION get_days_to_action(
    p_institution_number    IN INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_acquirer_reference    IN INT_SUNDRY_HISTORY.ACQUIRER_REFERENCE%TYPE,
    p_posting_date          IN SYS_POSTING_DATE.POSTING_DATE%TYPE,
    p_central_date          IN INT_SUNDRY_HISTORY.CENTRAL_DATE%TYPE DEFAULT NULL
  ) RETURN NUMBER
  IS
    v_days_to_action NUMBER;
  BEGIN
    v_days_to_action := BW_DISPUTE_AUTOMATION.get_days_diff(p_institution_number, p_acquirer_reference, p_posting_date, p_central_date);

    RETURN (
      CASE
      WHEN v_days_to_action > 0 THEN 0
      ELSE v_days_to_action * -1
      END
    );
  END;

  FUNCTION get_days_to_card_scheme_deadline(
    p_case_number         IN INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_institution_number  IN INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_posting_date        IN SYS_POSTING_DATE.POSTING_DATE%TYPE
  ) RETURN VARCHAR2
  IS
    v_setup_data_err      EXCEPTION;
    v_remaining_date      VARCHAR2(100);
    v_deadline_passed     VARCHAR2(100);
    v_rule_action         INT_SUNDRY_HISTORY.RULE_ACTION%TYPE;
    v_sundry_type         INT_SUNDRY_HISTORY.SUNDRY_TYPE%TYPE;
    v_central_date        INT_SUNDRY_HISTORY.CENTRAL_DATE%TYPE;
    v_sundry_reason       INT_SUNDRY_HISTORY.CHARGEBACK_REASON%TYPE;
    PRAGMA EXCEPTION_INIT(v_setup_data_err, -20034);
  BEGIN
    SELECT
      rule_action,
      sundry_type,
      central_date,
      sundry_reason
    INTO
      v_rule_action,
      v_sundry_type,
      v_central_date,
      v_sundry_reason
    FROM (
      SELECT
        ISH.RULE_ACTION,
        ISH.SUNDRY_TYPE,
        ISH.CENTRAL_DATE,
        COALESCE(ISH.MISCELLANEOUS_REASON,
          get_chargeback_reason(ISH.INSTITUTION_NUMBER, ISH.ACQUIRER_REFERENCE),
          get_retrieval_reason(ISH.INSTITUTION_NUMBER, ISH.ACQUIRER_REFERENCE)) SUNDRY_REASON
      FROM INT_SUNDRY_HISTORY ISH
      WHERE ISH.INSTITUTION_NUMBER = p_institution_number
      AND ISH.CASE_NUMBER = p_case_number
      AND ISH.RULE_ACTION IN (c_dispute_tran_action,
                              c_miscellaneous_action,
                              c_pre_arbitration_rejected,
                              c_raise_pre_arbitration,
                              c_in_pre_compliance,
                              c_in_compliance,
                              c_pre_arbitration_raised_by_issuer,
                              c_arbitration_raised_by_issuer)
      ORDER BY ISH.SUNDRY_HISTORY_ID DESC
    )
    WHERE ROWNUM < 2;

    BW_DISPUTE_AUTOMATION.days_to_card_scheme_deadline(
      p_ext_acquirer => '999',
      p_posting_date => p_posting_date,
      p_rule_action => v_rule_action,
      p_sundry_type => v_sundry_type,
      p_case_number => p_case_number,
      p_central_date => v_central_date,
      p_instno => p_institution_number,
      p_sundry_reason => v_sundry_reason,
      p_remaing_days => v_remaining_date,
      p_deadline_passed => v_deadline_passed
    );

    RETURN v_remaining_date;
  EXCEPTION
    WHEN v_setup_data_err THEN RETURN '';
  END;


 FUNCTION is_rapid_dispute_resolution_case(
    p_case_number         IN INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_institution_number  IN INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE
  ) RETURN INT_SUNDRY_TRANSACTIONS.RAPID_DISPUTE_FLAG%TYPE
  IS
    v_rdr_flag INT_SUNDRY_TRANSACTIONS.RAPID_DISPUTE_FLAG%TYPE;
  BEGIN
    SELECT
      RAPID_DISPUTE_FLAG
    INTO
      v_rdr_flag
    FROM (
      SELECT
        NVL(SND.RAPID_DISPUTE_FLAG, c_confirmation_no) AS RAPID_DISPUTE_FLAG
        FROM INT_SUNDRY_TRANSACTIONS SND,
             INT_SUNDRY_HISTORY HST
        WHERE HST.INSTITUTION_NUMBER = p_institution_number
        AND HST.CASE_NUMBER = p_case_number
        AND HST.SUNDRY_HISTORY_ID IN (SELECT MAX(SUNDRY_HISTORY_ID)
                                      FROM INT_SUNDRY_HISTORY
                                      WHERE INSTITUTION_NUMBER = HST.INSTITUTION_NUMBER
                                      AND CASE_NUMBER = HST.CASE_NUMBER
                                      AND CARD_ORGANIZATION = c_visa_card_org
                                      AND SUNDRY_TYPE = c_first_chargeback
                                      AND SUNDRY_TRANSACTION_SLIP IS NOT NULL)
        AND SND.INSTITUTION_NUMBER = HST.INSTITUTION_NUMBER
        AND SND.SUNDRY_TRANSACTION_SLIP = HST.SUNDRY_TRANSACTION_SLIP
        AND SND.SUNDRY_TYPE = c_first_chargeback
    );

    RETURN v_rdr_flag;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      RETURN c_confirmation_no;
  END;

  FUNCTION get_case_directory(
    p_case_number         INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_institution_number  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_source              VARCHAR2
  ) RETURN VARCHAR2
  IS
    v_full_path   VARCHAR2(1200);
    v_path_query  VARCHAR2(1000);
  BEGIN
    v_path_query := 'SELECT SSS.' || p_source || ' || ''/'' || REPLACE(LIC.INSTITUTION_NAME,'' '','''') || ''/'' || BWT.CARD_ORGANIZATION || ''/Case'' || HIS.CASE_NUMBER || ''/''
                    FROM INT_SUNDRY_HISTORY HIS,
                      BWT_CARD_ORGANIZATION BWT,
                      SYS_INSTITUTION_LICENCE LIC,
                      SYS_SUNDRY_SETTINGS SSS
                    WHERE HIS.CASE_NUMBER = :case_number
                    AND HIS.INSTITUTION_NUMBER = :institution_number
                    AND HIS.RULE_ACTION = :case_header_action
                    AND BWT.INDEX_FIELD = HIS.CARD_ORGANIZATION
                    AND BWT.INSTITUTION_NUMBER = HIS.INSTITUTION_NUMBER
                    AND BWT.LANGUAGE = ''USA''
                    AND LIC.INSTITUTION_NUMBER = HIS.INSTITUTION_NUMBER
                    AND SSS.INSTITUTION_NUMBER = HIS.INSTITUTION_NUMBER';

    EXECUTE IMMEDIATE v_path_query
    INTO v_full_path
    USING p_case_number, p_institution_number, c_case_header_action;

    RETURN v_full_path;
  END;

  FUNCTION get_web_case_directory(
    p_case_number         INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_institution_number  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE
  ) RETURN VARCHAR2
  IS
    v_full_path_web VARCHAR2(4000);
  BEGIN
    v_full_path_web := get_case_directory(p_case_number, p_institution_number, c_web_source);

    RETURN v_full_path_web;
  END;

  FUNCTION get_portal_case_directory(
    p_case_number         INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_institution_number  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE
  ) RETURN VARCHAR2
  IS
    v_full_path_portal VARCHAR2(4000);
  BEGIN
    v_full_path_portal := get_case_directory(p_case_number, p_institution_number, c_portal_source);

    RETURN v_full_path_portal;
  END;

  FUNCTION get_bo_case_directory(
    p_case_number         INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_institution_number  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE
  ) RETURN VARCHAR2
  IS
    v_full_path_bo VARCHAR2(4000);
  BEGIN
    v_full_path_bo := get_case_directory(p_case_number, p_institution_number, c_bo_source);

    RETURN v_full_path_bo;
  END;

  FUNCTION get_last_sundry_type(
    p_case_number         INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_institution_number  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE
    ) RETURN INT_SUNDRY_HISTORY.SUNDRY_TYPE%TYPE
    IS
      v_latest_sundry_type INT_SUNDRY_HISTORY.SUNDRY_TYPE%TYPE;
    BEGIN
      BEGIN
        SELECT HST.SUNDRY_TYPE
        INTO v_latest_sundry_type
        FROM INT_SUNDRY_HISTORY HST
        WHERE HST.INSTITUTION_NUMBER = p_institution_number
        AND HST.CASE_NUMBER = p_case_number
        AND HST.SUNDRY_HISTORY_ID = (SELECT MAX(SUNDRY_HISTORY_ID)
                                    FROM INT_SUNDRY_HISTORY
                                    WHERE INSTITUTION_NUMBER = HST.INSTITUTION_NUMBER
                                          AND CASE_NUMBER = HST.CASE_NUMBER
                                          AND SUNDRY_TYPE IS NOT NULL);
      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20001, 'There are no records that contain a sundry type for this case');
      END;
      RETURN v_latest_sundry_type;
    END;

  FUNCTION get_closed_cases(p_institution_number INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE)
    RETURN TBL_CLOSED_CASES
    PIPELINED AS
    CURSOR tbl_cursor IS
          SELECT SUNDRY_HISTORY.CASE_NUMBER,
                 SUNDRY_HISTORY.SENT_FLAG
          FROM INT_TRANSACTIONS TRANS, INT_SUNDRY_HISTORY SUNDRY_HISTORY
          WHERE
            TRANS.INSTITUTION_NUMBER = p_institution_number
            AND TRANS.INSTITUTION_NUMBER = SUNDRY_HISTORY.INSTITUTION_NUMBER
            AND TRANS.TRANSACTION_SLIP = SUNDRY_HISTORY.NUMBER_ORIGINAL_SLIP
            AND SUNDRY_HISTORY.CASE_STATUS = c_closed_case_status;
    BEGIN
      FOR cursor_record IN tbl_cursor LOOP
        PIPE ROW (cursor_record);
      END LOOP;
    END;

  FUNCTION get_case_financials(p_institution_number VARCHAR2, p_acquirer_reference VARCHAR2)
    RETURN TBL_CASE_FINANCIALS
    PIPELINED AS
    CURSOR tbl_cursor IS
          SELECT
            IT.TRANSACTION_SLIP                                                                            AS TRANSACTION_SLIP,
            IT.TRANSACTION_CATEGORY                                                                        AS TRANSACTION_CATEGORY,
            IT.TRANSACTION_TYPE                                                                            AS TRANSACTION_TYPE,
            IT.TRAN_CURRENCY                                                                               AS TRANSACTION_CURRENCY,
            IT.TRAN_AMOUNT_GR                                                                              AS TRANSACTION_AMOUNT,
            CAS.ACCOUNT_TYPE_ID                                                                            AS ACCOUNT_NAME,
            CASE
              WHEN IT.REVERSAL_FLAG = c_confirmation_yes
              THEN DECODE(IT.DR_CR_INDICATOR, c_credit, c_debit, c_credit)
              ELSE IT.DR_CR_INDICATOR
            END                                                                                            AS DR_CR_INDICATOR,
            IT.TRANSACTION_STATUS                                                                          AS TRANSACTION_STATUS,
            IT.RECORD_DATE || ' ' || IT.TIME_TRANSACTION                                                   AS DATE_TIME,
            IT.SETTLEMENT_AMOUNT_GR                                                                        AS SETTLEMENT_AMOUNT,
            IT.SETTLEMENT_CURRENCY                                                                         AS SETTLEMENT_CURRENCY

          FROM INT_TRANSACTIONS IT,
                CAS_CLIENT_ACCOUNT CAS

          WHERE
            IT.INSTITUTION_NUMBER = p_institution_number
            AND IT.ACQUIRER_REFERENCE = p_acquirer_reference
            AND IT.TRANSACTION_CLASS = c_clearing_trans

            AND CAS.INSTITUTION_NUMBER = IT.INSTITUTION_NUMBER
            AND CAS.ACCT_NUMBER = IT.ACCT_NUMBER

          ORDER BY IT.FILE_NUMBER;

    BEGIN
      FOR cursor_record IN tbl_cursor LOOP
        PIPE ROW (cursor_record);
      END LOOP;
    END;

  FUNCTION get_shared_documents_by_acquirer(p_institution_number  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
                                            p_case_number         INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
                                            p_current_language    BWT_FILE_EXTENSION.LANGUAGE%TYPE DEFAULT 'USA',
                                            p_default_language    BWT_FILE_EXTENSION.LANGUAGE%TYPE DEFAULT 'USA')
    RETURN TBL_SHARED_DOCUMENTS_ACQUIRER
    PIPELINED AS
    CURSOR tbl_cursor IS
          SELECT DOC.SUNDRY_HISTORY_ID,
                 DOC. DESCRIPTIVE_DOC_NAME,
                 EXT.FILE_EXTENSION
          FROM INT_SUNDRY_HISTORY DOC,
               BWT_FILE_EXTENSION EXT
          WHERE DOC.INSTITUTION_NUMBER = p_institution_number
            AND DOC.CASE_NUMBER = p_case_number
            AND DOC.RULE_ACTION = c_attach_document_action
            AND DOC.MERCHANT_VISIBLE <> c_confirmation_no
            AND DOC.USER_ID IS NOT NULL
            AND EXT.INSTITUTION_NUMBER (+) = DOC.INSTITUTION_NUMBER
            AND EXT.INDEX_FIELD (+) = DOC.FILE_EXTENSION_ID
            AND EXT.LANGUAGE (+) = CASE
                                  WHEN EXISTS(SELECT INDEX_FIELD
                                              FROM BWT_FILE_EXTENSION
                                              WHERE LANGUAGE = p_current_language
                                                    AND INSTITUTION_NUMBER = EXT.INSTITUTION_NUMBER
                                                    AND INDEX_FIELD = EXT.INDEX_FIELD)
                                    THEN p_current_language
                                  ELSE p_default_language
                                  END;

    BEGIN
      FOR cursor_record IN tbl_cursor LOOP
        PIPE ROW (cursor_record);
      END LOOP;
    END;

  FUNCTION get_correspondence_ids(p_institution_number  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
                                  p_case_number         INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE)
      RETURN TBL_CORRESPONDENCE_ID
      PIPELINED AS
        CURSOR tbl_cursor IS
          SELECT SUNDRY_HISTORY_ID
          FROM INT_SUNDRY_HISTORY
          WHERE INSTITUTION_NUMBER = p_institution_number
          AND CASE_NUMBER = p_case_number
          AND NOTE_TEXT IS NOT NULL
          AND RULE_ACTION IN (c_acquirer_sent_message, c_merchant_sent_message); --'266', '267'

    BEGIN
      FOR cursor_record IN tbl_cursor LOOP
        PIPE ROW (cursor_record);
      END LOOP;
    END;

    FUNCTION has_unread_messages(p_institution_number  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
                                 p_case_number         INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
                                p_is_portal_request   VARCHAR2 DEFAULT 'FALSE')
    RETURN VARCHAR2
    AS
      v_unread_messages_flag VARCHAR2(3) := c_confirmation_no;
      v_count NUMBER;
    BEGIN
      SELECT COUNT(*)
      INTO v_count
      FROM INT_SUNDRY_HISTORY HIS
      WHERE HIS.CASE_NUMBER = p_case_number
        AND HIS.INSTITUTION_NUMBER = p_institution_number
        AND HIS.SUNDRY_HISTORY_ID IN (SELECT SUNDRY_HISTORY_ID
                                      FROM TABLE (get_correspondence_ids(HIS.INSTITUTION_NUMBER, HIS.CASE_NUMBER)))
        AND ((p_is_portal_request = 'FALSE' AND HIS.MP_USER_ID IS NOT NULL) OR
              (p_is_portal_request = 'TRUE' AND HIS.USER_ID IS NOT NULL))
        AND NVL(HIS.ACKNOWLEDGED_FLAG, c_confirmation_no) <> c_confirmation_yes;

        IF v_count > 0
        THEN
          v_unread_messages_flag := c_confirmation_yes;
         END IF;

      RETURN v_unread_messages_flag;
    END;

  FUNCTION get_correspondence(p_institution_number INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
                              p_case_number INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
                              p_is_portal_request VARCHAR2 DEFAULT 'FALSE',
                              p_current_language BWT_FILE_EXTENSION.LANGUAGE%TYPE DEFAULT 'USA',
                              p_default_language BWT_FILE_EXTENSION.LANGUAGE%TYPE DEFAULT 'USA')
    RETURN TBL_CASE_CORRESPONDENCE PIPELINED
    IS
    out_rec row_case_correspondence;
    rec_document row_case_correspondence;
    is_doc_attached boolean;

      CURSOR cur_correspondence IS
        SELECT SUNDRY_HISTORY_ID,
                RULE_ACTION,
                SUNDRY_TYPE,
                RECORD_DATE,
                RECORD_TIME,
                NOTE_TEXT,
                DECODE (HIS.USER_ID, '', c_merchant_actor, c_bank_actor) AS SENDER,
                CASE WHEN USER_ID IS NULL
                    THEN
                        (SELECT TRADE_NAME
                        FROM CIS_CLIENT_DETAILS
                        WHERE INSTITUTION_NUMBER = HIS.INSTITUTION_NUMBER
                        AND CLIENT_NUMBER = HIS.CLIENT_NUMBER)
                    ELSE
                        (SELECT USERNAME
                        FROM SYS_USER_INFORMATION
                        WHERE USERID = HIS.USER_ID)
                END AS SENDER_NAME,
            (CASE WHEN p_is_portal_request = 'FALSE'-- Viewer is the Bank
                THEN
                    CASE WHEN HIS.USER_ID IS NULL AND NVL(HIS.ACKNOWLEDGED_FLAG, c_confirmation_no) <> c_confirmation_yes
                        THEN c_confirmation_yes  -- Show unread
                        ELSE NULL
                    END
                ELSE
                    CASE WHEN p_is_portal_request = 'TRUE' -- Viewer is the Merchant
                        THEN
                            CASE WHEN
                                HIS.USER_ID IS NOT NULL AND NVL(HIS.ACKNOWLEDGED_FLAG, c_confirmation_no) <> c_confirmation_yes
                                THEN c_confirmation_yes  -- Show unread
                                ELSE NULL
                            END
                    END
            END) AS UNREAD,
            (CASE WHEN p_is_portal_request = 'FALSE'-- Viewer is the Bank
                THEN
                    CASE WHEN HIS.USER_ID IS NOT NULL AND NVL(HIS.ACKNOWLEDGED_FLAG, c_confirmation_no) = c_confirmation_yes
                        THEN c_confirmation_yes  -- Show acknowledged
                        ELSE NULL
                    END
                ELSE
                    CASE WHEN p_is_portal_request = 'TRUE' -- Viewer is the Merchant
                        THEN
                            CASE WHEN
                                HIS.USER_ID IS NULL AND NVL(HIS.ACKNOWLEDGED_FLAG, c_confirmation_no) = c_confirmation_yes
                                THEN c_confirmation_yes  -- Show acknowledged
                                ELSE NULL
                            END
                    END
            END) AS ACKNOWLEDGED,
            NULL AS DOCUMENT,
            NULL AS PARENT_SUNDRY_HISTORY_ID,
            NULL AS DOCUMENT_LOCATION,
            NULL AS DESCRIPTIVE_DOC_NAME,
            NULL AS FILE_EXTENSION,
            NULL AS FILE_EXTENSION_ID,
            NULL AS MERCHANT_VISIBLE
        FROM INT_SUNDRY_HISTORY HIS
        WHERE INSTITUTION_NUMBER = p_institution_number
        AND CASE_NUMBER = p_case_number
        AND HIS.SUNDRY_HISTORY_ID IN (SELECT SUNDRY_HISTORY_ID
                                        FROM TABLE (get_correspondence_ids(HIS.INSTITUTION_NUMBER, HIS.CASE_NUMBER)))
        ORDER BY SUNDRY_HISTORY_ID;

      CURSOR cur_case_document(cp_sundry_history_id varchar2) IS
        SELECT  DOC.SUNDRY_HISTORY_ID,
              DOC.RULE_ACTION,
              NULL AS SUNDRY_TYPE,
              NULL AS RECORD_DATE,
              NULL AS RECORD_TIME,
              NULL AS NOTE_TEXT,
              NULL AS SENDER,
              NULL AS SENDER_NAME,
              NULL AS UNREAD,
              NULL AS ACKNOWLEGED,
              NULL AS DOCUMENT,
              cp_sundry_history_id AS PARENT_SUNDRY_HISTORY_ID,
              DOC.DOCUMENT_LOCATION,
              DOC.DESCRIPTIVE_DOC_NAME,
              EXT.FILE_EXTENSION,
              DOC.FILE_EXTENSION_ID,
              NVL(DOC.MERCHANT_VISIBLE, c_confirmation_yes) AS MERCHANT_VISIBLE
        FROM INT_SUNDRY_HISTORY DOC,
           BWT_FILE_EXTENSION EXT
        WHERE DOC.INSTITUTION_NUMBER = p_institution_number
        AND DOC.CASE_NUMBER = p_case_number
        AND DOC.SUNDRY_HISTORY_ID < cp_sundry_history_id
        AND EXT.INSTITUTION_NUMBER (+) = DOC.INSTITUTION_NUMBER
        AND EXT.INDEX_FIELD (+) = DOC.FILE_EXTENSION_ID
        AND EXT.LANGUAGE (+) = CASE
                              WHEN EXISTS(SELECT INDEX_FIELD
                                          FROM BWT_FILE_EXTENSION
                                          WHERE LANGUAGE = p_current_language
                                                AND INSTITUTION_NUMBER = EXT.INSTITUTION_NUMBER
                                                AND INDEX_FIELD = EXT.INDEX_FIELD)
                                THEN p_current_language
                              ELSE p_default_language
                              END
        ORDER BY DOC.SUNDRY_HISTORY_ID DESC;

  BEGIN

    FOR rec IN cur_correspondence LOOP
      out_rec.sundry_history_id := rec.sundry_history_id;
      out_rec.rule_action := rec.rule_action;
      out_rec.sundry_type := rec.sundry_type;
      out_rec.record_date := rec.record_date;
      out_rec.record_time := rec.record_time;
      out_rec.note_text := rec.note_text;
      out_rec.sender := rec.sender;
      out_rec.sender_name := rec.sender_name;
      out_rec.unread := rec.unread;
      out_rec.acknowledged := rec.acknowledged;
      out_rec.merchant_visible := rec.merchant_visible;

    is_doc_attached := false;

      FOR rec_document in cur_case_document(rec.sundry_history_id) LOOP
        -- populate attached document/s if any:
        IF rec_document.rule_action = c_attach_document_action AND
           rec_document.merchant_visible = c_confirmation_yes THEN
          is_doc_attached := true;
          rec_document.rule_action := null; --set as null from the output since such records will always have 106 rule action
          PIPE ROW (rec_document);
        ELSE
          EXIT;
        END IF;
      END LOOP;

      IF is_doc_attached THEN
        -- set document to 001 for cur_correspondence, if 106 record is found
        out_rec.document := c_confirmation_yes;
      ELSE
        out_rec.document := null;
      END IF;

      PIPE ROW (out_rec);

    END LOOP;
  END;

  FUNCTION get_stored_case_documents(p_institution_number INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
                                     p_case_number INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE)
    RETURN TBL_CASE_DOCUMENTS PIPELINED
    AS
      return_record ROW_CASE_DOCUMENTS;
      CURSOR tbl_cursor IS
        SELECT
          HIS.SUNDRY_HISTORY_ID                                               AS SUNDRY_HISTORY_ID,
          HIS.DOCUMENT_LOCATION                                               AS DOCUMENT_LOCATION,
          HIS.DESCRIPTIVE_DOC_NAME || '.' || EXT.FILE_EXTENSION               AS DOCUMENT_NAME,
          (CASE WHEN HIS.MP_USER_ID IS NOT NULL
              THEN 'Merchant Upload'
              ELSE CASE WHEN UPPER(HIS.DESCRIPTIVE_DOC_NAME) LIKE '%_LETTER%'
                      THEN 'Generated Letter'
                      ELSE 'Agent Upload'
                  END
          END)                                                                AS DOCUMENT_TYPE,
          (CASE WHEN HIS.MP_USER_ID IS NOT NULL
              THEN 'Merchant Portal'
              ELSE CASE WHEN HIS.USER_ID = (SELECT USERID FROM SYS_USER_INFORMATION
                                            WHERE LOWER(USERNAME)= 'automation')
                      THEN 'Automation'
                      ELSE 'Web Interface'
                  END
          END)                                                                AS ORIGIN,
          USR.USERNAME                                                        AS UPLOADED_BY,
          HIS.RECORD_DATE || ' ' || HIS.RECORD_TIME                           AS UPLOAD_DATE,
          HIS.DESCRIPTIVE_DOC_NAME                                            AS DESCRIPTIVE_DOC_NAME,
          HIS.FILE_EXTENSION_ID                                               AS FILE_EXTENSION_ID
        FROM INT_SUNDRY_HISTORY HIS,
            SYS_USER_INFORMATION USR,
            BWT_FILE_EXTENSION EXT
        WHERE HIS.CASE_NUMBER = p_case_number
          AND HIS.INSTITUTION_NUMBER = p_institution_number
          AND HIS.RULE_ACTION = c_add_document_to_case_storage
          AND USR.USERID (+) = HIS.USER_ID
          AND EXT.INSTITUTION_NUMBER = HIS.INSTITUTION_NUMBER
          AND EXT.INDEX_FIELD = HIS.FILE_EXTENSION_ID
        ORDER BY SUNDRY_HISTORY_ID;
    BEGIN
      FOR cursor_record IN tbl_cursor LOOP
        -- populate the return record with the values retrieved
        return_record.SUNDRY_HISTORY_ID := cursor_record.SUNDRY_HISTORY_ID;
        return_record.DOCUMENT_LOCATION := cursor_record.DOCUMENT_LOCATION;
        return_record.DOCUMENT_NAME := cursor_record.DOCUMENT_NAME;
        return_record.DOCUMENT_TYPE := cursor_record.DOCUMENT_TYPE;
        return_record.DOCUMENT_ORIGIN := cursor_record.ORIGIN;
        return_record.UPLOADED_BY := cursor_record.UPLOADED_BY;
        return_record.UPLOAD_DATE := cursor_record.UPLOAD_DATE;

        BEGIN
          -- query for at least a single matching document attachment record which is merchant visible
          SELECT MERCHANT_VISIBLE, SEEN_BY_MERCHANT
          INTO return_record.MERCHANT_VISIBLE, return_record.SEEN_BY_MERCHANT
          FROM INT_SUNDRY_HISTORY ATCH
          WHERE INSTITUTION_NUMBER = p_institution_number
            AND CASE_NUMBER = p_case_number
            AND DOCUMENT_LOCATION = cursor_record.DOCUMENT_LOCATION
            AND DESCRIPTIVE_DOC_NAME = cursor_record.DESCRIPTIVE_DOC_NAME
            AND FILE_EXTENSION_ID = cursor_record.FILE_EXTENSION_ID
            AND RULE_ACTION = c_attach_document_action
            AND SUNDRY_HISTORY_ID = (SELECT MAX(SUNDRY_HISTORY_ID)
                                          FROM INT_SUNDRY_HISTORY
                                          WHERE INSTITUTION_NUMBER = ATCH.INSTITUTION_NUMBER
                                          AND CASE_NUMBER = ATCH.CASE_NUMBER
                                          AND DOCUMENT_LOCATION = ATCH.DOCUMENT_LOCATION
                                          AND DESCRIPTIVE_DOC_NAME = ATCH.DESCRIPTIVE_DOC_NAME
                                          AND FILE_EXTENSION_ID = ATCH.FILE_EXTENSION_ID
                                          AND RULE_ACTION = ATCH.RULE_ACTION
                                          AND MERCHANT_VISIBLE = c_confirmation_yes);
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            return_record.MERCHANT_VISIBLE := '';
            return_record.SEEN_BY_MERCHANT := '';
        END;
        PIPE ROW (return_record);
      END LOOP;
    END;

  FUNCTION get_sequence_number(
    p_sequence_id          varchar2,
    p_institution_number  varchar2
    ) RETURN varchar2  AS
    sequence_number               VARCHAR2(100);
    BEGIN
      sequence_number := BW_CODE_LIBRARY.BWS_GET_SEQ_NUMBER(p_sequence_id, 1, p_institution_number);
      RETURN sequence_number;
  END;

  FUNCTION get_range_tier_settings(p_institution_number VARCHAR2)
    RETURN TBL_SYS_SUNDRY_RANGE_TIERS
    PIPELINED AS
      CURSOR tbl_cursor IS
        SELECT RANGE_ID, RANGE_NAME, RANGE_START, RANGE_END
        FROM SYS_SUNDRY_RANGE_TIERS
        WHERE INSTITUTION_NUMBER = p_institution_number
        ORDER BY RANGE_END, RANGE_START;

      BEGIN
        FOR cursor_record IN tbl_cursor LOOP
          PIPE ROW (cursor_record);
        END LOOP;
  END;

  FUNCTION is_range_in_use(p_institution_number SYS_SUNDRY_TEAM_ASSIGN.INSTITUTION_NUMBER%TYPE,
                           p_range_id SYS_SUNDRY_TEAM_ASSIGN.RANGE_ID%TYPE)
    RETURN VARCHAR2
    AS
      v_count         NUMBER := 0;
      v_in_use_flag   BWT_CONFIRMATION.INDEX_FIELD%TYPE;
    BEGIN
      SELECT COUNT(*)
      INTO v_count
      FROM SYS_SUNDRY_TEAM_ASSIGN
      WHERE INSTITUTION_NUMBER = p_institution_number
      AND RANGE_ID = p_range_id;

      IF v_count > 0 THEN
        v_in_use_flag := c_confirmation_yes;
      ELSE
        v_in_use_flag := c_confirmation_no;
      END IF;

      RETURN v_in_use_flag;
    END;

  FUNCTION validate_range_data(p_tier_name  SYS_SUNDRY_RANGE_TIERS.RANGE_NAME%TYPE,
                               p_range_start  SYS_SUNDRY_RANGE_TIERS.RANGE_START%TYPE,
                               p_range_end  SYS_SUNDRY_RANGE_TIERS.RANGE_END%TYPE)
    RETURN BOOLEAN
    AS
      v_is_valid         BOOLEAN := TRUE;
    BEGIN
      IF p_tier_name IS NOT NULL AND p_range_start IS NOT NULL AND p_range_end IS NOT NULL THEN
        IF IsNumeric(p_range_start) = false OR IsNumeric(p_range_end) = false THEN
          v_is_valid := FALSE;
        ELSIF TO_NUMBER(p_range_start) >= TO_NUMBER(p_range_end) THEN
          v_is_valid := FALSE;
        END IF;
      ELSE
        v_is_valid := FALSE;
      END IF;

      RETURN v_is_valid;
    END;

  PROCEDURE save_range_tier_settings(
      p_institution_number    IN    SYS_DISPUTE_QUEUES.INSTITUTION_NUMBER%TYPE,
      p_setting_data          IN    CLOB,
      p_session_id            IN    NUMBER DEFAULT NULL,
      p_source                IN    GUI_MASK_MENU.MASK_NAME%TYPE,
      p_delete_list           IN    BW_DISPUTES_ARRAY,
      o_output                OUT   VARCHAR2
    ) AS
      v_setting_array       JSON_ARRAY_T;
      setting_row_obj       JSON_OBJECT_T;
      v_transaction_id      NUMBER;
      v_range_start         VARCHAR2(20);
      v_range_end           VARCHAR2(20);
      v_range_id            SYS_SUNDRY_RANGE_TIERS.RANGE_ID%TYPE;
      v_tier_name           VARCHAR2(30);
      v_params              BW_DISPUTES_CORE.ASSOC_ARRAY;
      v_sql                 VARCHAR2(4000);
      v_count               NUMBER := 0;
    BEGIN
      o_output := null;
      v_setting_array := JSON_Array_T(p_setting_data);

      IF p_session_id IS NOT NULL THEN
        BW_AMC.AMC_AUDIT.InitTransaction(v_transaction_id);
      END IF;

      IF v_setting_array.is_Array  THEN
        SAVEPOINT sp_start_transaction;
        v_params('INSTITUTION_NUMBER') := p_institution_number;

        DELETE FROM SYS_SUNDRY_RANGE_TIERS
        WHERE INSTITUTION_NUMBER = p_institution_number
        AND RANGE_ID IN (SELECT * FROM TABLE(p_delete_list));

        FOR i IN 0..v_setting_array.get_size - 1 LOOP
          setting_row_obj := TREAT(v_setting_array.get(i) AS JSON_OBJECT_T);
          IF setting_row_obj.IS_OBJECT THEN
            v_tier_name := setting_row_obj.get_string('RANGE_NAME');
            v_range_start := setting_row_obj.get_string('RANGE_START');
            v_range_end := setting_row_obj.get_string('RANGE_END');
            v_range_id := setting_row_obj.get_string('RANGE_ID');

            IF validate_range_data(v_tier_name, v_range_start, v_range_end) = FALSE THEN
              o_output := 'invalid-manual-range';
              ROLLBACK TO SAVEPOINT sp_start_transaction;
              EXIT;
            END IF;

            v_params('RANGE_NAME') := v_tier_name;
            v_params('RANGE_START') := v_range_start;
            v_params('RANGE_END') := v_range_end;
            v_params('INSTITUTION_NUMBER') := p_institution_number;

            -- If range_id is empty, it is a newly added record, otherwise it should already exist, so update the record.
            IF v_range_id IS NULL  THEN
              v_params('RANGE_ID') := get_sequence_number(bw_const.SEQ_DISPUTE_AMT_RANGE_ID, p_institution_number);
              v_sql := 'INSERT INTO SYS_SUNDRY_RANGE_TIERS (INSTITUTION_NUMBER, RANGE_ID, RANGE_NAME, RANGE_START,
                                              RANGE_END)
                      VALUES (:INSTITUTION_NUMBER, :RANGE_ID, :RANGE_NAME, :RANGE_START, :RANGE_END)';

              BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);
            ELSE
              SELECT COUNT(*)
              INTO v_count
              FROM SYS_SUNDRY_RANGE_TIERS
              WHERE INSTITUTION_NUMBER = p_institution_number
              AND RANGE_ID = v_range_id;

              IF v_count > 0 THEN
                v_params('RANGE_ID') := v_range_id;
                v_sql := 'UPDATE SYS_SUNDRY_RANGE_TIERS
                          SET RANGE_NAME = :RANGE_NAME,
                              RANGE_START = :RANGE_START,
                              RANGE_END = :RANGE_END
                          WHERE INSTITUTION_NUMBER = :INSTITUTION_NUMBER
                          AND RANGE_ID = :RANGE_ID';

                BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);
              ELSE
                o_output := 'non-existent-range-id';
                ROLLBACK TO SAVEPOINT sp_start_transaction;
                EXIT;
              END IF;
            END IF;

          END IF;
        END LOOP;
      ELSE
        o_output := 'invalid-range-tier-parameter';
      END IF;

  EXCEPTION
    WHEN OTHERS THEN
      raise_application_error(-20001,'An error was encountered while saving range tier settings: '||SQLERRM);
      ROLLBACK TO SAVEPOINT sp_start_transaction;
      o_output := '000';
  END;

  PROCEDURE reset_sent_flag(
      p_institution_number IN  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
      p_case_number        IN INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE
    )
    AS
    BEGIN
      UPDATE INT_SUNDRY_HISTORY
          SET SENT_FLAG = NULL
          WHERE
            INSTITUTION_NUMBER = p_institution_number
            AND CASE_NUMBER = p_case_number
            AND RULE_ACTION = c_case_header_action;
  END;

  PROCEDURE link_document_to_case(
    p_institution_number   IN  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_number          IN  INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_file_ext_id          IN  BWT_FILE_EXTENSION.FILE_EXTENSION%TYPE,
    p_note_text            IN  INT_SUNDRY_HISTORY.NOTE_TEXT%TYPE,
    p_descriptive_doc_name IN  INT_SUNDRY_HISTORY.DESCRIPTIVE_DOC_NAME%TYPE,
    p_rule_action          IN  VARCHAR2,
    p_audit_trail          IN  INT_SUNDRY_HISTORY.AUDIT_TRAIL%TYPE DEFAULT NULL,
    p_user_id              IN  INT_SUNDRY_HISTORY.USER_ID%TYPE DEFAULT NULL,
    p_mp_user_id           IN  INT_SUNDRY_HISTORY.MP_USER_ID%TYPE DEFAULT NULL,
    p_document_location    IN  VARCHAR2 DEFAULT NULL,
    o_document_name        OUT INT_SUNDRY_HISTORY.DOCUMENT_NAME%TYPE,
    p_source               IN  GUI_MASK_MENU.MASK_NAME%TYPE DEFAULT NULL,
    p_session_id           IN  NUMBER DEFAULT NULL,
    p_transaction_id       IN  NUMBER DEFAULT NULL,
    p_sundry_history_id    IN  INT_SUNDRY_HISTORY.SUNDRY_HISTORY_ID%TYPE DEFAULT NULL,
    p_posting_date         IN  SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format)
  )
  AS
    v_sql                 VARCHAR2(4000);
    v_params              BW_DISPUTES_CORE.ASSOC_ARRAY;
    v_transaction_id      NUMBER;
    v_file_ext_id         BWT_FILE_EXTENSION.INDEX_FIELD%TYPE;
    v_is_dcd_enabled      VARCHAR2(1);
    v_acquirer_reference  INT_SUNDRY_HISTORY.ACQUIRER_REFERENCE%TYPE;
    v_card_organization   INT_SUNDRY_HISTORY.CARD_ORGANIZATION%TYPE;
    v_client_number       INT_SUNDRY_HISTORY.CLIENT_NUMBER%TYPE;
    v_case_status         INT_SUNDRY_HISTORY.CASE_STATUS%TYPE;
    v_sundry_type         INT_SUNDRY_HISTORY.SUNDRY_TYPE%TYPE;
    v_sent_flag           INT_SUNDRY_HISTORY.SENT_FLAG%TYPE;
    BEGIN
      IF p_session_id IS NOT NULL
      THEN
        IF p_transaction_id IS NULL
        THEN
          BW_AMC.AMC_AUDIT.InitTransaction(v_transaction_id);
        ELSE
          v_transaction_id := p_transaction_id;
        END IF;
      END IF;

      v_params('INSTITUTION_NUMBER') := p_institution_number;
      v_params('CASE_NUMBER') := p_case_number;

      IF p_sundry_history_id IS NULL THEN
        v_params('SUNDRY_HISTORY_ID') := get_sequence_number(c_sundry_history_id_seq, p_institution_number);
      ELSE
        v_params('SUNDRY_HISTORY_ID') := p_sundry_history_id;
      END IF;

      v_params('RECORD_DATE') := p_posting_date;
      v_params('RECORD_TIME') := TO_CHAR(SYSDATE, c_time_format);
      v_params('AUDIT_TRAIL') := p_audit_trail;
      v_params('RULE_ACTION') := p_rule_action; -- 106 - attach document | 244 - attach internal document
      v_params('SUNDRY_STATUS') := BWTPAD('BWT_SUNDRY_STATUS', '008'); -- Approved
      v_params('PARENT_CASE_NUMBER') := '00000000000';
      v_params('MERCHANT_VISIBLE') := '';

      IF p_rule_action = c_attach_document_action
      THEN
        v_params('MERCHANT_VISIBLE') := c_confirmation_yes;
      END IF;

      SELECT INDEX_FIELD
      INTO v_file_ext_id
      FROM BWT_FILE_EXTENSION
      WHERE INSTITUTION_NUMBER = p_institution_number
            AND UPPER(FILE_EXTENSION) = UPPER(p_file_ext_id)
            AND LANGUAGE = 'USA';

      v_params('FILE_EXTENSION_ID') := v_file_ext_id;
      v_params('USER_ID') := p_user_id;
      v_params('MP_USER_ID') := p_mp_user_id;
      v_params('NOTE_TEXT') := p_note_text;
      v_params('DESCRIPTIVE_DOC_NAME') := p_descriptive_doc_name;

      SELECT ACQUIRER_REFERENCE, CARD_ORGANIZATION, CLIENT_NUMBER, CASE_STATUS, SENT_FLAG
      INTO v_acquirer_reference, v_card_organization, v_client_number, v_case_status, v_sent_flag
      FROM TABLE(get_case_header_details(p_institution_number, p_case_number));

      v_params('ACQUIRER_REFERENCE') := v_acquirer_reference;
      v_params('CARD_ORGANIZATION') := v_card_organization;
      v_params('CLIENT_NUMBER') := v_client_number;
      v_params('CASE_STATUS') := v_case_status;

      --Reset the Sent Flag when attaching a document to a closed case.
      IF v_case_status = c_closed_case_status AND v_sent_flag = c_confirmation_yes
        THEN
          reset_sent_flag(p_institution_number, p_case_number);
      END IF;

      v_sundry_type := get_last_sundry_type(p_case_number, p_institution_number);
      v_params('SUNDRY_TYPE') := v_sundry_type;
      o_document_name := p_descriptive_doc_name;
      v_params('DOCUMENT_LOCATION') := p_document_location;

      v_sql := 'INSERT INTO INT_SUNDRY_HISTORY (INSTITUTION_NUMBER, SUNDRY_HISTORY_ID, CASE_NUMBER, CASE_STATUS,
                                                RECORD_DATE, RECORD_TIME, AUDIT_TRAIL, NOTE_TEXT, RULE_ACTION,
                                                SUNDRY_STATUS, PARENT_CASE_NUMBER, DOCUMENT_LOCATION, FILE_EXTENSION_ID,
                                                USER_ID, MP_USER_ID, DESCRIPTIVE_DOC_NAME, ACQUIRER_REFERENCE, CARD_ORGANIZATION,
                                                CLIENT_NUMBER, SUNDRY_TYPE, MERCHANT_VISIBLE)

                VALUES (:INSTITUTION_NUMBER, :SUNDRY_HISTORY_ID, :CASE_NUMBER, :CASE_STATUS, :RECORD_DATE, :RECORD_TIME,
                        :AUDIT_TRAIL, :NOTE_TEXT, :RULE_ACTION, :SUNDRY_STATUS, :PARENT_CASE_NUMBER, :DOCUMENT_LOCATION,
                        :FILE_EXTENSION_ID, :USER_ID, :MP_USER_ID, :DESCRIPTIVE_DOC_NAME, :ACQUIRER_REFERENCE, :CARD_ORGANIZATION,
                        :CLIENT_NUMBER, :SUNDRY_TYPE, :MERCHANT_VISIBLE)';

      BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);
    END;

  PROCEDURE insert_document_details(
    p_institution_number   IN  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_number          IN  INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_file_ext_id          IN  BWT_FILE_EXTENSION.FILE_EXTENSION%TYPE,
    p_note_text            IN  INT_SUNDRY_HISTORY.NOTE_TEXT%TYPE,
    p_descriptive_doc_name IN  INT_SUNDRY_HISTORY.DESCRIPTIVE_DOC_NAME%TYPE,
    p_audit_trail          IN  INT_SUNDRY_HISTORY.AUDIT_TRAIL%TYPE DEFAULT NULL,
    p_user_id              IN  INT_SUNDRY_HISTORY.MP_USER_ID%TYPE DEFAULT NULL,
    p_document_location    IN  VARCHAR2 DEFAULT NULL,
    o_document_name        OUT INT_SUNDRY_HISTORY.DOCUMENT_NAME%TYPE,
    p_source               IN  GUI_MASK_MENU.MASK_NAME%TYPE DEFAULT NULL,
    p_session_id           IN  NUMBER DEFAULT NULL,
    p_transaction_id       IN  NUMBER DEFAULT NULL,
    p_sundry_history_id    IN  INT_SUNDRY_HISTORY.SUNDRY_HISTORY_ID%TYPE DEFAULT NULL
  )
  AS
    v_case_numbers            bw_disputes_array;
    v_file_ext_id             bw_disputes_array;
    v_descriptive_doc_name    bw_disputes_array;
    v_document_location       bw_disputes_array;
    v_success                 BWT_CONFIRMATION.INDEX_FIELD%TYPE;
  BEGIN
    v_case_numbers := bw_disputes_array(p_case_number);
    v_file_ext_id := bw_disputes_array(p_file_ext_id);
    v_descriptive_doc_name := bw_disputes_array(p_descriptive_doc_name);
    v_document_location := bw_disputes_array(p_document_location);

    -- First make sure to insert a record which represents the document upload
    insert_document_upload_details(p_institution_number => p_institution_number,
                                  p_case_numbers => v_case_numbers,
                                  p_file_ext_ids => v_file_ext_id,
                                  p_descriptive_doc_names => v_descriptive_doc_name,
                                  p_document_locations => v_document_location,
                                  p_user_id => null,
                                  p_mp_user_id => p_user_id,
                                  p_source => p_source,
                                  p_session_id => p_session_id,
                                  p_transaction_id => p_transaction_id,
                                  o_success => v_success,
                                  p_sundry_history_id => p_sundry_history_id);

    IF v_success = c_confirmation_yes THEN
      -- Then insert a record which represents the document attachment (rule action = 106)
      link_document_to_case(p_institution_number, p_case_number, p_file_ext_id, p_note_text, p_descriptive_doc_name,
                  c_attach_document_action, p_audit_trail, null, p_user_id, p_document_location, o_document_name, p_source,
                  p_session_id, p_transaction_id, '');
    END IF;
  END;

  PROCEDURE insert_operator_document_details(
    p_institution_number   IN  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_number          IN  INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_file_ext_id          IN  BWT_FILE_EXTENSION.FILE_EXTENSION%TYPE,
    p_descriptive_doc_name IN  INT_SUNDRY_HISTORY.DESCRIPTIVE_DOC_NAME%TYPE,
    p_audit_trail          IN  INT_SUNDRY_HISTORY.AUDIT_TRAIL%TYPE DEFAULT NULL,
    p_user_id              IN  INT_SUNDRY_HISTORY.USER_ID%TYPE DEFAULT NULL,
    p_document_location    IN  VARCHAR2 DEFAULT NULL,
    p_posting_date         IN  SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format),
    o_document_name        OUT INT_SUNDRY_HISTORY.DOCUMENT_NAME%TYPE,
    p_source               IN  GUI_MASK_MENU.MASK_NAME%TYPE DEFAULT NULL,
    p_session_id           IN  NUMBER DEFAULT NULL,
    p_transaction_id       IN  NUMBER DEFAULT NULL,
    p_sundry_history_id    IN  INT_SUNDRY_HISTORY.SUNDRY_HISTORY_ID%TYPE DEFAULT NULL
  )
  AS
    BEGIN
      link_document_to_case(p_institution_number => p_institution_number,
                            p_case_number => p_case_number,
                            p_file_ext_id => p_file_ext_id,
                            p_note_text => '',
                            p_descriptive_doc_name => p_descriptive_doc_name,
                            p_rule_action => c_attach_document_action,
                            p_audit_trail => p_audit_trail,
                            p_user_id => p_user_id,
                            p_document_location => p_document_location,
                            o_document_name => o_document_name,
                            p_source => p_source,
                            p_session_id => p_session_id,
                            p_transaction_id => p_transaction_id,
                            p_sundry_history_id => p_sundry_history_id,
                            p_posting_date => p_posting_date);
    END;

  PROCEDURE insert_internal_document_details(
    p_institution_number   IN  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_number          IN  INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_file_ext_id          IN  BWT_FILE_EXTENSION.FILE_EXTENSION%TYPE,
    p_descriptive_doc_name IN  INT_SUNDRY_HISTORY.DESCRIPTIVE_DOC_NAME%TYPE,
    p_audit_trail          IN  INT_SUNDRY_HISTORY.AUDIT_TRAIL%TYPE DEFAULT NULL,
    p_user_id              IN  INT_SUNDRY_HISTORY.USER_ID%TYPE DEFAULT NULL,
    p_document_location    IN  VARCHAR2 DEFAULT NULL,
    p_posting_date         IN  SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format),
    o_document_name        OUT INT_SUNDRY_HISTORY.DOCUMENT_NAME%TYPE,
    p_source               IN  GUI_MASK_MENU.MASK_NAME%TYPE DEFAULT NULL,
    p_session_id           IN  NUMBER DEFAULT NULL,
    p_transaction_id       IN  NUMBER DEFAULT NULL,
    p_sundry_history_id    IN  INT_SUNDRY_HISTORY.SUNDRY_HISTORY_ID%TYPE DEFAULT NULL
  )
  AS
    BEGIN
      link_document_to_case(p_institution_number => p_institution_number,
                            p_case_number => p_case_number,
                            p_file_ext_id => p_file_ext_id,
                            p_note_text => '',
                            p_descriptive_doc_name => p_descriptive_doc_name,
                            p_rule_action => c_attach_internal_document_action,
                            p_audit_trail => p_audit_trail,
                            p_user_id => p_user_id,
                            p_document_location => p_document_location,
                            o_document_name => o_document_name,
                            p_source => p_source,
                            p_session_id => p_session_id,
                            p_transaction_id => p_transaction_id,
                            p_sundry_history_id => p_sundry_history_id,
                            p_posting_date => p_posting_date);
    END;

  PROCEDURE update_case_status(
    p_institution_number IN INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_number        IN INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_new_status         IN INT_SUNDRY_HISTORY.SUNDRY_STATUS%TYPE,
    p_posting_date       IN SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format),
    p_current_language   IN VARCHAR2 DEFAULT 'USA',
    p_default_language   IN VARCHAR2 DEFAULT 'USA',
    p_source             IN GUI_MASK_MENU.MASK_NAME%TYPE DEFAULT NULL,
    p_transaction_id     IN NUMBER DEFAULT NULL,
    p_session_id         IN NUMBER DEFAULT NULL,
    p_central_date       IN INT_SUNDRY_HISTORY.CENTRAL_DATE%TYPE DEFAULT NULL,
    p_audit_trail        IN INT_SUNDRY_HISTORY.AUDIT_TRAIL%TYPE,
    p_new_sundry_type    IN INT_SUNDRY_HISTORY.SUNDRY_TYPE%TYPE,
    p_user_id            IN INT_SUNDRY_HISTORY.USER_ID%TYPE DEFAULT NULL,
    p_mp_user_id         IN INT_SUNDRY_HISTORY.MP_USER_ID%TYPE DEFAULT NULL
  )
  AS
    v_sql                VARCHAR2(4000);
    v_params             BW_DISPUTES_CORE.ASSOC_ARRAY;
    v_sundry_status_text BWT_CASE_STATUS.CASE_STATUS%TYPE;
    v_case_status        INT_SUNDRY_HISTORY.CASE_STATUS%TYPE;
    v_acquirer_reference INT_SUNDRY_HISTORY.ACQUIRER_REFERENCE%TYPE;
    v_card_organization  INT_SUNDRY_HISTORY.CARD_ORGANIZATION%TYPE;
    v_client_number      INT_SUNDRY_HISTORY.CLIENT_NUMBER%TYPE;
    BEGIN
      v_params('INSTITUTION_NUMBER') := p_institution_number;
      v_params('SUNDRY_HISTORY_ID') := get_sequence_number(c_sundry_history_id_seq,
                                                                          p_institution_number);
      v_params('CASE_NUMBER') := p_case_number;
      v_params('RECORD_DATE') := p_posting_date;
      v_params('RECORD_TIME') := TO_CHAR(SYSDATE, c_time_format);
      v_params('RULE_ACTION') := c_status_change;
      v_params('CASE_NEW_STATUS') := p_new_status;

      -- Get details from case header
      SELECT CASE_STATUS, ACQUIRER_REFERENCE, CARD_ORGANIZATION, CLIENT_NUMBER
      INTO v_case_status, v_acquirer_reference, v_card_organization, v_client_number
      FROM TABLE(get_case_header_details(p_institution_number, p_case_number));

      v_params('ACQUIRER_REFERENCE') := v_acquirer_reference;
      v_params('CARD_ORGANIZATION') := v_card_organization;
      v_params('CLIENT_NUMBER') := v_client_number;
      v_params('AUDIT_TRAIL') := p_audit_trail;
      v_params('SUNDRY_TYPE') := p_new_sundry_type;

      -- If the new status is closed, or else the existing case status is currently
      -- closed and the new status will be opened, we must update the sundry_status
      -- to closed or approved respectively
      IF (p_new_status = c_closed_case_status) -- Closed
        OR (v_case_status = c_closed_case_status AND p_new_status = BWTPAD('BWT_CASE_STATUS', '001')) -- Closed to Opened
      THEN

        IF p_new_status = c_closed_case_status -- Closed
        THEN
          v_params('SUNDRY_STATUS') := BWTPAD('BWT_SUNDRY_STATUS', '015'); -- Closed
        ELSE
          v_params('SUNDRY_STATUS') := BWTPAD('BWT_SUNDRY_STATUS', '008'); -- Approved
          reset_sent_flag(p_institution_number, p_case_number); -- Reset SENT_FLAG to Null since case is being reopened
        END IF;

        SELECT PROCESSING_STATUS
        INTO v_sundry_status_text
        FROM BWT_SUNDRY_STATUS STATUS
        WHERE
          INDEX_FIELD = v_params('SUNDRY_STATUS')
          AND INSTITUTION_NUMBER = p_institution_number
          AND STATUS.LANGUAGE = CASE
                                WHEN EXISTS(SELECT INDEX_FIELD
                                            FROM BWT_SUNDRY_STATUS
                                            WHERE LANGUAGE = p_current_language
                                                  AND INSTITUTION_NUMBER = STATUS.INSTITUTION_NUMBER
                                                  AND INDEX_FIELD = STATUS.INDEX_FIELD)
                                  THEN p_current_language
                                ELSE p_default_language
                                END;

        v_params('NOTE_TEXT') := 'Case status changed to ' || v_sundry_status_text;
        v_params('CENTRAL_DATE') := p_central_date;
        v_params('USER_ID') := p_user_id;
        v_params('MP_USER_ID') := p_mp_user_id;

        v_sql := 'INSERT INTO INT_SUNDRY_HISTORY (INSTITUTION_NUMBER, SUNDRY_HISTORY_ID, CASE_NUMBER, RECORD_DATE,
                                                  RECORD_TIME, RULE_ACTION, CASE_STATUS, SUNDRY_STATUS, NOTE_TEXT,
                                                  CENTRAL_DATE, ACQUIRER_REFERENCE, CARD_ORGANIZATION, CLIENT_NUMBER,
                                                  AUDIT_TRAIL, SUNDRY_TYPE, USER_ID, MP_USER_ID)

                VALUES (:INSTITUTION_NUMBER, :SUNDRY_HISTORY_ID, :CASE_NUMBER, :RECORD_DATE, :RECORD_TIME, :RULE_ACTION,
                        :CASE_NEW_STATUS, :SUNDRY_STATUS, :NOTE_TEXT, :CENTRAL_DATE, :ACQUIRER_REFERENCE, :CARD_ORGANIZATION,
                        :CLIENT_NUMBER, :AUDIT_TRAIL, :SUNDRY_TYPE, :USER_ID, :MP_USER_ID)';

        BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, p_transaction_id);
      END IF;

      v_params('RULE_ACTION') := BWTPAD('BWT_ACTION_TAKEN', '110'); -- Open New Case

      -- If the update the case status of the header with the new status
      v_sql := 'UPDATE INT_SUNDRY_HISTORY
                   SET CASE_STATUS = :CASE_NEW_STATUS ';

      -- Only update the sundry status if the new case status is closed, or else the existing case status is closed and the new status is opened
      IF (p_new_status = BWTPAD('BWT_CASE_STATUS', '003')) -- Closed
        OR (v_case_status = BWTPAD('BWT_CASE_STATUS', '003') AND p_new_status = BWTPAD('BWT_CASE_STATUS', '001'))
      THEN
        v_sql := v_sql || ', SUNDRY_STATUS = :SUNDRY_STATUS ';
      END IF;

      v_sql := v_sql || ' WHERE
                            RULE_ACTION = :RULE_ACTION
                            AND INSTITUTION_NUMBER = :INSTITUTION_NUMBER
                            AND CASE_NUMBER = :CASE_NUMBER';

      BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, p_transaction_id);
    END;

  PROCEDURE update_case_escalation(
    p_institution_number IN INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_number        IN INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_new_escalation     IN INT_SUNDRY_HISTORY.ESCALATED_FLAG%TYPE,
    p_source             IN GUI_MASK_MENU.MASK_NAME%TYPE DEFAULT NULL,
    p_transaction_id     IN NUMBER DEFAULT NULL,
    p_session_id         IN NUMBER DEFAULT NULL
  )
  AS
    v_sql                VARCHAR2(4000);
    v_params             BW_DISPUTES_CORE.ASSOC_ARRAY;
    BEGIN
      v_params('INSTITUTION_NUMBER') := p_institution_number;
      v_params('CASE_NUMBER') := p_case_number;
      v_params('CASE_NEW_ESCALATED_FLAG') := p_new_escalation;
      v_params('RULE_ACTION') := c_case_header_action; -- Open New Case

      IF p_new_escalation <> BWTPAD('BWT_CONFIRMATION', '999') -- If not n/a
      THEN
        -- Update the escalated flag of the header with the new escalated flag value
        v_sql := 'UPDATE INT_SUNDRY_HISTORY
                    SET ESCALATED_FLAG = :CASE_NEW_ESCALATED_FLAG
                    WHERE
                        RULE_ACTION = :RULE_ACTION
                        AND INSTITUTION_NUMBER = :INSTITUTION_NUMBER
                        AND CASE_NUMBER = :CASE_NUMBER';

        BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, p_transaction_id);
      END IF;
    END;

  PROCEDURE update_sundry_type(
    p_institution_number IN INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_number        IN INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_new_sundry_type    IN INT_SUNDRY_HISTORY.SUNDRY_TYPE%TYPE,
    p_posting_date       IN SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format),
    p_source             IN GUI_MASK_MENU.MASK_NAME%TYPE DEFAULT NULL,
    p_transaction_id     IN NUMBER DEFAULT NULL,
    p_session_id         IN NUMBER DEFAULT NULL,
    p_central_date       IN INT_SUNDRY_HISTORY.CENTRAL_DATE%TYPE DEFAULT NULL,
    p_audit_trail        IN INT_SUNDRY_HISTORY.AUDIT_TRAIL%TYPE,
    p_user_id            IN INT_SUNDRY_HISTORY.USER_ID%TYPE DEFAULT NULL,
    p_mp_user_id         IN INT_SUNDRY_HISTORY.MP_USER_ID%TYPE DEFAULT NULL
  )
  AS
    v_sql                 VARCHAR2(4000);
    v_params              BW_DISPUTES_CORE.ASSOC_ARRAY;
    v_acquirer_reference  INT_SUNDRY_HISTORY.ACQUIRER_REFERENCE%TYPE;
    v_card_organization   INT_SUNDRY_HISTORY.CARD_ORGANIZATION%TYPE;
    v_client_number       INT_SUNDRY_HISTORY.CLIENT_NUMBER%TYPE;
    v_sundry_status       INT_SUNDRY_HISTORY.SUNDRY_STATUS%TYPE;
    v_case_status         INT_SUNDRY_HISTORY.CASE_STATUS%TYPE;
    v_last_sundry_type    INT_SUNDRY_HISTORY.SUNDRY_TYPE%TYPE;
    v_portal_request      VARCHAR2(5) := 'FALSE';

    BEGIN
      IF p_mp_user_id IS NOT NULL
      THEN
        v_portal_request := 'TRUE';
      END IF;

      v_last_sundry_type := get_last_sundry_type(p_case_number, p_institution_number);

      -- Insert a misc action record only if the sundry type has actually changed.
      IF p_new_sundry_type <> v_last_sundry_type
      THEN
        v_params('INSTITUTION_NUMBER') := p_institution_number;
        v_params('SUNDRY_HISTORY_ID') := get_sequence_number(c_sundry_history_id_seq,
                                                                            p_institution_number);
        v_params('CASE_NUMBER') := p_case_number;
        v_params('RECORD_DATE') := p_posting_date;
        v_params('RECORD_TIME') := TO_CHAR(SYSDATE, c_time_format);
        v_params('RULE_ACTION') := c_miscellaneous_action; -- Miscellaneous Action
        v_params('NEW_SUNDRY_TYPE') := p_new_sundry_type;
        v_params('CENTRAL_DATE') := p_central_date;

        SELECT ACQUIRER_REFERENCE, CARD_ORGANIZATION, CLIENT_NUMBER, SUNDRY_STATUS, CASE_STATUS
        INTO v_acquirer_reference, v_card_organization, v_client_number, v_sundry_status, v_case_status
        FROM TABLE(get_case_header_details(p_institution_number, p_case_number));

        v_params('ACQUIRER_REFERENCE') := v_acquirer_reference;
        v_params('CARD_ORGANIZATION') := v_card_organization;
        v_params('CLIENT_NUMBER') := v_client_number;
        v_params('SUNDRY_STATUS') := v_sundry_status;
        v_params('CASE_STATUS') := v_case_status;
        v_params('AUDIT_TRAIL') := p_audit_trail;
        v_params('USER_ID') := p_user_id;
        v_params('MP_USER_ID') := p_mp_user_id;

        v_sql := 'INSERT INTO INT_SUNDRY_HISTORY (INSTITUTION_NUMBER, SUNDRY_HISTORY_ID, CASE_NUMBER, RECORD_DATE,
                                                  RECORD_TIME, RULE_ACTION, SUNDRY_TYPE, CENTRAL_DATE, ACQUIRER_REFERENCE,
                                                  CARD_ORGANIZATION, CLIENT_NUMBER, SUNDRY_STATUS, CASE_STATUS, AUDIT_TRAIL,
                                                  USER_ID, MP_USER_ID)

                  VALUES (:INSTITUTION_NUMBER, :SUNDRY_HISTORY_ID, :CASE_NUMBER, :RECORD_DATE, :RECORD_TIME,
                          :RULE_ACTION, :NEW_SUNDRY_TYPE, :CENTRAL_DATE, :ACQUIRER_REFERENCE, :CARD_ORGANIZATION, :CLIENT_NUMBER,
                          :SUNDRY_STATUS, :CASE_STATUS, :AUDIT_TRAIL, :USER_ID, :MP_USER_ID)';

        BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, p_transaction_id);
      END IF;
    END;

  FUNCTION get_revert_record(p_institution_number IN INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
                             p_case_number IN INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
                             p_action_taken IN BWT_ACTION_TAKEN.INDEX_FIELD%TYPE,
                             p_is_portal_request VARCHAR2
  ) RETURN ROW_REVERT_CASE
  IS
      v_last_action ROW_LAST_ACTOR_ACTION;
      v_action_to_revert_id INT_SUNDRY_HISTORY.SUNDRY_HISTORY_ID%TYPE;
      v_action_to_revert INT_SUNDRY_HISTORY.RULE_ACTION%TYPE;
      v_count NUMBER;
      row_revert_record ROW_REVERT_CASE;
      v_actor BWT_SUNDRY_ACTOR.INDEX_FIELD%TYPE := c_operator_actor;

  BEGIN
    BEGIN
      --Get the last occurrence of the record to revert
      IF p_action_taken = c_any_action THEN
        IF p_is_portal_request = 'TRUE' THEN
          v_actor := c_merchant_actor;
        END IF;

        v_last_action := get_last_action_by_actor(p_institution_number => p_institution_number,
                                                    p_case_number => p_case_number,
                                                    p_actor => v_actor,
                                                    p_is_portal_request => p_is_portal_request);
        v_action_to_revert_id := v_last_action.SUNDRY_HISTORY_ID;
        v_action_to_revert := v_last_action.RULE_ACTION;
      ELSE
        SELECT ISH.SUNDRY_HISTORY_ID, RULE_ACTION
        INTO v_action_to_revert_id, v_action_to_revert
        FROM INT_SUNDRY_HISTORY ISH
        WHERE ISH.INSTITUTION_NUMBER = p_institution_number
          AND ISH.CASE_NUMBER = p_case_number
          AND ISH.RULE_ACTION = p_action_taken
          AND ISH.SUNDRY_HISTORY_ID = (SELECT MAX(SUNDRY_HISTORY_ID)
                                      FROM INT_SUNDRY_HISTORY SUNDRY
                                      WHERE SUNDRY.INSTITUTION_NUMBER = ISH.INSTITUTION_NUMBER
                                      AND SUNDRY.CASE_NUMBER = ISH.CASE_NUMBER
                                      AND SUNDRY.RULE_ACTION = ISH.RULE_ACTION
                                      );
      END IF;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      RAISE_APPLICATION_ERROR(-20002, 'Requested action was not found');
    END;

    BEGIN
      --Check if last action taken by actor is not allowed to be reverted
      IF v_action_to_revert = c_transfer_action THEN -- '100'
        RAISE_APPLICATION_ERROR(-20003, 'A Transfer cannot be reverted');
      END IF;

      --Check if a dispute transaction record or a miscellaneous action exists after the retireved one
      SELECT COUNT(*)
      INTO v_count
      FROM INT_SUNDRY_HISTORY
      WHERE INSTITUTION_NUMBER = p_institution_number
        AND CASE_NUMBER = p_case_number
        AND RULE_ACTION IN (c_dispute_tran_action, c_miscellaneous_action)
        AND SUNDRY_HISTORY_ID > v_action_to_revert_id;

      IF v_count > 0
      THEN
        RAISE_APPLICATION_ERROR(-20004, 'The action has already been communicated with the card schemes');
      END IF;

      --Get the first previous record from the record retrieved by given rule action(1st query)
      SELECT ISH.ACQUIRER_REFERENCE,
             ISH.CARD_ORGANIZATION,
             ISH.CASE_NUMBER,
             ISH.CASE_STATUS,
             ISH.CENTRAL_DATE,
             ISH.CHARGEBACK_REASON,
             ISH.CLIENT_NUMBER,
             ISH.DESCRIPTIVE_DOC_NAME,
             ISH.DOCUMENT_LOCATION,
             ISH.ESCALATED_FLAG,
             ISH.INSTITUTION_NUMBER,
             ISH.MERCHANT_VISIBLE,
             ISH.MP_USER_ID,
             ISH.NOTE_TEXT,
             ISH.PARENT_CASE_NUMBER,
             ISH.RECORD_DATE,
             ISH.RECORD_TIME,
             ISH.RETRIEVAL_REASON,
             ISH.RULE_ACTION,
             ISH.RULE_ID,
             ISH.SUNDRY_HISTORY_ID,
             ISH.SUNDRY_STATUS,
             ISH.SUNDRY_TRANSACTION_SLIP,
             ISH.SUNDRY_TYPE,
             ISH.USER_ID
      INTO row_revert_record
      FROM INT_SUNDRY_HISTORY ISH
      WHERE ISH.INSTITUTION_NUMBER = p_institution_number
        AND ISH.CASE_NUMBER = p_case_number
        AND ISH.SUNDRY_HISTORY_ID = (SELECT MAX(SUNDRY_HISTORY_ID)
                                    FROM INT_SUNDRY_HISTORY SUNDRY
                                    WHERE SUNDRY.INSTITUTION_NUMBER = ISH.INSTITUTION_NUMBER
                                      AND SUNDRY.CASE_NUMBER = ISH.CASE_NUMBER
                                      AND SUNDRY.SUNDRY_HISTORY_ID < v_action_to_revert_id
                                      AND SUNDRY.RULE_ACTION NOT IN (SELECT * FROM
                                                                      TABLE(get_exclude_actions(c_non_revertable)))
                                    );
    RETURN row_revert_record;
    END;
  END;

  FUNCTION enter_revert_record(row_revert_record   IN ROW_REVERT_CASE,
                               p_posting_date      IN SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format),
                               p_current_language  IN VARCHAR2 DEFAULT 'USA',
                               p_default_language  IN VARCHAR2 DEFAULT 'USA',
                               p_central_date      IN INT_SUNDRY_HISTORY.CENTRAL_DATE%TYPE DEFAULT NULL,
                               p_source            IN GUI_MASK_MENU.MASK_NAME%TYPE DEFAULT NULL,
                               p_session_id        IN NUMBER DEFAULT NULL,
                               p_user_id           IN INT_SUNDRY_HISTORY.USER_ID%TYPE DEFAULT NULL,
                               p_mp_user_id        IN INT_SUNDRY_HISTORY.MP_USER_ID%TYPE DEFAULT NULL,
                               p_actor             IN SYS_SUNDRY_ACTION_SETUP.ACTOR%TYPE,
                               p_transaction_id    IN NUMBER DEFAULT NULL
    ) RETURN BOOLEAN
    IS
      v_audit_trail INT_SUNDRY_HISTORY.AUDIT_TRAIL%TYPE;
      v_sql         VARCHAR2(4000);
      v_params      BW_DISPUTES_CORE.ASSOC_ARRAY;
      v_header_case_status INT_SUNDRY_HISTORY.CASE_STATUS%TYPE;

  BEGIN

    v_audit_trail := BW_CODE_LIBRARY.CreateAuditTrail(p_user_id, c_station_number);

    SELECT CASE_STATUS
    INTO v_header_case_status
    FROM TABLE(get_case_header_details(row_revert_record.institution_number, row_revert_record.case_number));

    IF v_header_case_status != row_revert_record.case_status
    THEN
      update_case_status(
                          row_revert_record.institution_number,
                          row_revert_record.case_number,
                          row_revert_record.case_status,
                          p_posting_date,
                          p_current_language,
                          p_default_language,
                          p_source,
                          p_transaction_id,
                          p_session_id,
                          p_central_date,
                          v_audit_trail,
                          row_revert_record.sundry_type,
                          p_user_id,
                          p_mp_user_id);
    END IF;

    -- Only operators can update the sundry type, thus if the actor is an operator call the function.
    IF p_actor = c_operator_actor
    THEN
      v_audit_trail := BW_CODE_LIBRARY.CreateAuditTrail(p_user_id, c_station_number);

      update_sundry_type(
                          row_revert_record.institution_number,
                          row_revert_record.case_number,
                          row_revert_record.sundry_type,
                          p_posting_date,
                          p_source,
                          p_transaction_id,
                          p_session_id,
                          p_central_date,
                          v_audit_trail,
                          p_user_id,
                          p_mp_user_id
                        );
    END IF;

    v_audit_trail := BW_CODE_LIBRARY.CreateAuditTrail(p_user_id, c_station_number);

    v_params('INSTITUTION_NUMBER') := row_revert_record.INSTITUTION_NUMBER;
    v_params('RECORD_DATE') := p_posting_date;
    v_params('RECORD_TIME') := TO_CHAR(SYSDATE, c_time_format);
    v_params('CENTRAL_DATE') := row_revert_record.CENTRAL_DATE;
    v_params('USER_ID') := '999999';
    v_params('MP_USER_ID') := '';
    v_params('ACQUIRER_REFERENCE') :=  row_revert_record.ACQUIRER_REFERENCE;
    v_params('CARD_ORGANIZATION') := row_revert_record.CARD_ORGANIZATION;
    v_params('CASE_NUMBER') := row_revert_record.CASE_NUMBER;
    v_params('CHARGEBACK_REASON') := row_revert_record.CHARGEBACK_REASON;
    v_params('CLIENT_NUMBER') := row_revert_record.CLIENT_NUMBER;
    v_params('CASE_STATUS') := row_revert_record.CASE_STATUS;
    v_params('SUNDRY_STATUS') := row_revert_record.SUNDRY_STATUS;
    v_params('SUNDRY_TYPE') := row_revert_record.SUNDRY_TYPE;
    v_params('SUNDRY_TRANSACTION_SLIP') := row_revert_record.SUNDRY_TRANSACTION_SLIP;
    v_params('ESCALATED_FLAG') := row_revert_record.ESCALATED_FLAG;
    v_params('RULE_ACTION') := row_revert_record.RULE_ACTION;
    v_params('RULE_ID') := row_revert_record.RULE_ID;
    v_params('DOCUMENT_LOCATION') := row_revert_record.DOCUMENT_LOCATION;
    v_params('DESCRIPTIVE_DOC_NAME') := row_revert_record.DESCRIPTIVE_DOC_NAME;
    v_params('PARENT_CASE_NUMBER') := row_revert_record.PARENT_CASE_NUMBER;
    v_params('SUNDRY_HISTORY_ID') := get_sequence_number(c_sundry_history_id_seq, row_revert_record.INSTITUTION_NUMBER);
    v_params('NOTE_TEXT') := '';
    v_params('MERCHANT_VISIBLE') := row_revert_record.MERCHANT_VISIBLE;
    v_params('AUDIT_TRAIL') := v_audit_trail;

    v_sql := 'INSERT INTO INT_SUNDRY_HISTORY (INSTITUTION_NUMBER, RECORD_DATE, RECORD_TIME, USER_ID,
                                              MP_USER_ID, ACQUIRER_REFERENCE, CARD_ORGANIZATION, CASE_NUMBER,
                                              CLIENT_NUMBER, CASE_STATUS, SUNDRY_STATUS, SUNDRY_TYPE, ESCALATED_FLAG,
                                              RULE_ACTION, RULE_ID, DOCUMENT_LOCATION, DESCRIPTIVE_DOC_NAME, PARENT_CASE_NUMBER,
                                              SUNDRY_HISTORY_ID, NOTE_TEXT, MERCHANT_VISIBLE, AUDIT_TRAIL, CENTRAL_DATE, CHARGEBACK_REASON, SUNDRY_TRANSACTION_SLIP)

              VALUES (:INSTITUTION_NUMBER, :RECORD_DATE, :RECORD_TIME, :USER_ID,
                      :MP_USER_ID, :ACQUIRER_REFERENCE, :CARD_ORGANIZATION, :CASE_NUMBER,
                      :CLIENT_NUMBER, :CASE_STATUS, :SUNDRY_STATUS, :SUNDRY_TYPE, :ESCALATED_FLAG,
                      :RULE_ACTION, :RULE_ID, :DOCUMENT_LOCATION, :DESCRIPTIVE_DOC_NAME, :PARENT_CASE_NUMBER,
                      :SUNDRY_HISTORY_ID, :NOTE_TEXT, :MERCHANT_VISIBLE, :AUDIT_TRAIL, :CENTRAL_DATE, :CHARGEBACK_REASON, :SUNDRY_TRANSACTION_SLIP)';

    BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, p_transaction_id);

    RETURN true;
  END;

  PROCEDURE take_action(
    p_actor              IN SYS_SUNDRY_ACTION_SETUP.ACTOR%TYPE,
    p_institution_number IN INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_number        IN INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_note_text          IN INT_SUNDRY_HISTORY.NOTE_TEXT%TYPE,
    p_card_org           IN SYS_SUNDRY_ACTION_SETUP.CARD_ORGANIZATION%TYPE,
    p_sundry_type        IN SYS_SUNDRY_ACTION_SETUP.SUNDRY_TYPE%TYPE,
    p_action             IN SYS_SUNDRY_ACTION_SETUP.ACTION%TYPE,
    p_escalated_flag     IN INT_SUNDRY_HISTORY.ESCALATED_FLAG%TYPE,
    p_previous_action    IN SYS_SUNDRY_ACTION_SETUP.PREVIOUS_ACTION%TYPE,
    p_uploaded_docs      IN BOOLEAN DEFAULT FALSE,
    p_fees               IN VARCHAR2 DEFAULT 'FALSE',
    p_posting_date       IN SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format),
    p_chargeback_reason  IN SYS_SUNDRY_ACTION_SETUP.CHARGEBACK_REASON_CODE%TYPE DEFAULT NULL,
    p_current_language   IN VARCHAR2 DEFAULT 'USA',
    p_default_language   IN VARCHAR2 DEFAULT 'USA',
    p_case_status        IN VARCHAR2 DEFAULT NULL,
    p_session_id         IN NUMBER DEFAULT NULL,
    p_source             IN GUI_MASK_MENU.MASK_NAME%TYPE DEFAULT NULL,
    p_central_date       IN INT_SUNDRY_HISTORY.CENTRAL_DATE%TYPE DEFAULT NULL,
    p_audit_trail        IN INT_SUNDRY_HISTORY.AUDIT_TRAIL%TYPE DEFAULT NULL,
    p_user_id            IN INT_SUNDRY_HISTORY.USER_ID%TYPE DEFAULT NULL,
    p_mp_user_id         IN INT_SUNDRY_HISTORY.MP_USER_ID%TYPE DEFAULT NULL,
    p_risk_accepted      IN INT_SUNDRY_HISTORY.RISK_ACCEPTED%TYPE DEFAULT NULL,
    p_action_date        IN INT_SUNDRY_HISTORY.PLANNED_REFUND_DATE%TYPE DEFAULT NULL
  )
  AS
    v_sql                       VARCHAR2(4000);
    v_transaction_id            NUMBER;
    v_new_status                SYS_SUNDRY_ACTION_SETUP.NEW_CASE_STATUS%TYPE;
    v_new_sundry_type           SYS_SUNDRY_ACTION_SETUP.SUNDRY_TYPE%TYPE;
    v_evidence_required         SYS_SUNDRY_ACTION_SETUP.EVIDENCE_REQUIRED%TYPE;
    v_notes_required            SYS_SUNDRY_ACTION_SETUP.NOTES_REQUIRED%TYPE;
    v_fee_confirmation          SYS_SUNDRY_ACTION_SETUP.FEE_CONFIRMATION%TYPE;
    v_new_escalated_flag        SYS_SUNDRY_ACTION_SETUP.NEW_ESCALATED_FLAG%TYPE;
    v_params                    BW_DISPUTES_CORE.ASSOC_ARRAY;
    v_last_chargeback_reason    SYS_SUNDRY_ACTION_SETUP.CHARGEBACK_REASON_CODE%TYPE;
    v_chargeback_reason         SYS_SUNDRY_ACTION_SETUP.CHARGEBACK_REASON_CODE%TYPE;
    v_acquirer_reference        INT_SUNDRY_HISTORY.ACQUIRER_REFERENCE%TYPE;
    v_card_organization         INT_SUNDRY_HISTORY.CARD_ORGANIZATION%TYPE;
    v_client_number             INT_SUNDRY_HISTORY.CLIENT_NUMBER%TYPE;
    v_sundry_status             INT_SUNDRY_HISTORY.SUNDRY_STATUS%TYPE;
    v_case_status               INT_SUNDRY_HISTORY.CASE_STATUS%TYPE;
    v_revert_action             SYS_SUNDRY_ACTION_SETUP.REVERT_ACTION%TYPE;
    v_action_to_revert          SYS_SUNDRY_ACTION_SETUP.ACTION_TO_REVERT%TYPE;
    v_central_date_required     SYS_SUNDRY_ACTION_SETUP.CENTRAL_DATE_REQUIRED%TYPE;
    v_requires_date             SYS_SUNDRY_ACTION_SETUP.REQUIRES_DATE%TYPE;
    v_last_central_date         INT_SUNDRY_HISTORY.CENTRAL_DATE%TYPE;
    v_risk_acceptance_required  SYS_SUNDRY_ACTION_SETUP.RISK_ACCEPTANCE_REQUIRED%TYPE;
    v_portal_request            VARCHAR2(5) := 'FALSE';
    v_revert_success            BOOLEAN;
    v_action_date               DATE;
    row_revert_record_details   ROW_REVERT_CASE;
    v_days_to_action            NUMBER;
    BEGIN


      IF p_session_id IS NOT NULL
      THEN
        BW_AMC.AMC_AUDIT.InitTransaction(v_transaction_id);
      END IF;

      -- This was added because chargeback reason is now a primary key and we still want to retain the use of the bwtPad function to retrieve the 999 value.
      IF p_chargeback_reason IS NULL
      THEN
        v_last_chargeback_reason := BWTPAD('BWT_CHARGEBACK_REASON', '999'); -- 999: All.
      ELSE
        v_last_chargeback_reason := p_chargeback_reason;
      END IF;

      BEGIN
        SELECT
          NEW_SUNDRY_TYPE,
          NEW_CASE_STATUS,
          EVIDENCE_REQUIRED,
          NOTES_REQUIRED,
          FEE_CONFIRMATION,
          NEW_ESCALATED_FLAG,
          REVERT_ACTION,
          ACTION_TO_REVERT,
          CENTRAL_DATE_REQUIRED,
          REQUIRES_DATE,
          RISK_ACCEPTANCE_REQUIRED
      INTO v_new_sundry_type, v_new_status, v_evidence_required, v_notes_required, v_fee_confirmation, v_new_escalated_flag, v_revert_action, v_action_to_revert, v_central_date_required, v_requires_date, v_risk_acceptance_required
      FROM TABLE (get_actions(p_institution_number, p_card_org, p_actor, p_sundry_type, p_case_number, p_escalated_flag, p_previous_action,
                               v_last_chargeback_reason, p_current_language, p_default_language, p_case_status))
      WHERE ACTION_ID = BWTPAD('BWT_ACTION_TAKEN', p_action);

      EXCEPTION
       WHEN NO_DATA_FOUND THEN
          RAISE_APPLICATION_ERROR(-20001, 'This action ('|| p_action || ') cannot be taken on this case.');
      END;

      IF v_central_date_required = c_confirmation_no AND p_central_date IS NOT NULL
      THEN
        RAISE_APPLICATION_ERROR(-20001, 'Central date is not required but was provided.');
      END IF;

      IF v_central_date_required = c_confirmation_yes AND p_central_date IS NULL
      THEN
        RAISE_APPLICATION_ERROR(-20001, 'Central date is required but not provided.');
      END IF;

      IF v_central_date_required = c_confirmation_yes AND p_central_date > p_posting_date
      THEN
        RAISE_APPLICATION_ERROR(-20001, 'The Central Date cannot be larger than ' || p_posting_date || ' .');
      END IF;

      IF v_evidence_required = c_confirmation_no AND p_uploaded_docs = TRUE
      THEN
        RAISE_APPLICATION_ERROR(-20001, 'Evidence is not required but was provided.');
      END IF;

      IF v_evidence_required = c_confirmation_yes AND p_uploaded_docs = FALSE
      THEN
        RAISE_APPLICATION_ERROR(-20001, 'Evidence is required but was not provided');
      END IF;

      IF v_notes_required = c_confirmation_no AND p_note_text IS NOT NULL
      THEN
        RAISE_APPLICATION_ERROR(-20001, 'Notes are not required but was provided.');
      END IF;

      IF v_notes_required = c_confirmation_yes AND p_note_text IS NULL
      THEN
        RAISE_APPLICATION_ERROR(-20001, 'Notes are required but was not provided.');
      END IF;

      IF v_fee_confirmation = c_confirmation_no AND UPPER(p_fees) = 'TRUE'
      THEN
        RAISE_APPLICATION_ERROR(-20001, 'Fees confirmation is not required but was accepted.');
      END IF;

      IF v_fee_confirmation = c_confirmation_yes AND UPPER(p_fees) <> 'TRUE'
      THEN
        RAISE_APPLICATION_ERROR(-20001, 'Fees confirmation is required but was not accepted.');
      END IF;

      IF v_revert_action = c_confirmation_no AND v_action_to_revert IS NOT NULL
      THEN
        RAISE_APPLICATION_ERROR(-20001, 'Action to revert is not required but was provided.');
      END IF;

      IF v_revert_action = c_confirmation_yes AND v_action_to_revert IS NULL
      THEN
        RAISE_APPLICATION_ERROR(-20001, 'Action to revert is required but was not provided.');
      END IF;

      -- If the current action does not have a new case type setting, then get the latest sundry_type of this case.
      IF v_new_sundry_type is null
      THEN
        IF p_mp_user_id IS NOT NULL
        THEN
          v_portal_request := 'TRUE';
        END IF;

        v_new_sundry_type := get_last_sundry_type(p_case_number, p_institution_number);
      END IF;

      IF v_requires_date = c_confirmation_yes
      THEN
        IF p_action_date IS NULL
        THEN
          RAISE_APPLICATION_ERROR(-20001, 'Action date is required but it was not provided');
        ELSE
          BEGIN
            v_action_date := TO_DATE(p_action_date,'YYYYMMDD');
            --Output an error when an exception is found when trying to convert value to date
            EXCEPTION
            WHEN OTHERS THEN
                RAISE_APPLICATION_ERROR(-20001, 'The action date provided is in an incorrect format');
          END;

          IF p_action_date < p_posting_date
          THEN
            RAISE_APPLICATION_ERROR(-20001, 'The action date should be greater than the posting date.');
          END IF;
        END IF;
      ELSIF v_requires_date = c_confirmation_no AND p_action_date IS NOT NULL
      THEN
        RAISE_APPLICATION_ERROR(-20001, 'Action date is not required for this action but was still passed');
      END IF;

      SELECT
      ACQUIRER_REFERENCE,
      CARD_ORGANIZATION,
      CLIENT_NUMBER,
      SUNDRY_STATUS,
      CASE_STATUS
      INTO v_acquirer_reference, v_card_organization, v_client_number, v_sundry_status, v_case_status
      FROM TABLE(get_case_header_details(p_institution_number, p_case_number));

      SELECT LAST_CENTRAL_DATE.CENTRAL_DATE
      INTO v_last_central_date
      FROM INT_SUNDRY_HISTORY LAST_CENTRAL_DATE
      WHERE LAST_CENTRAL_DATE.INSTITUTION_NUMBER = p_institution_number
      AND LAST_CENTRAL_DATE.SUNDRY_HISTORY_ID = (SELECT MAX(SUNDRY_HISTORY_ID)
                                                  FROM INT_SUNDRY_HISTORY
                                                  WHERE INSTITUTION_NUMBER = LAST_CENTRAL_DATE.INSTITUTION_NUMBER
                                                  AND ACQUIRER_REFERENCE = v_acquirer_reference
                                                  AND CENTRAL_DATE IS NOT NULL);

      v_days_to_action := get_days_to_action(p_institution_number, v_acquirer_reference, p_posting_date, v_last_central_date);

      IF v_days_to_action <= 0
        AND p_risk_accepted <> BWTPAD('BWT_CONFIRMATION', '001')
        AND v_risk_acceptance_required = BWTPAD('BWT_CONFIRMATION', '001')
      THEN
        RAISE_APPLICATION_ERROR(-20001, 'Merchant''s days to action have expired and risk was not accepted with this action.');
      END IF;

      IF v_revert_action = c_confirmation_yes
      THEN
        row_revert_record_details := get_revert_record(p_institution_number, p_case_number, v_action_to_revert, v_portal_request);
      END IF;

      -- Always update the case status of the case header record. If the new case status is 'closed' then also updates
      -- the sundry status. But some actions, like case escalation, should not update the case status, thus would have
      -- an empty new status value.
      IF length(v_new_status) > 0
      THEN
        update_case_status(p_institution_number, p_case_number, v_new_status, p_posting_date, p_current_language,
                          p_default_language, p_source, v_transaction_id, p_session_id, p_central_date, p_audit_trail,
                          v_new_sundry_type, p_user_id, p_mp_user_id);
        v_params('CASE_NEW_STATUS') := v_new_status;
      ELSE
        v_params('CASE_NEW_STATUS') := v_case_status; -- from case header
      END IF;

      -- Only operators can update the sundry type, thus if the actor is an operator call the function.
      IF p_actor = c_operator_actor
      THEN
        update_sundry_type(p_institution_number, p_case_number, v_new_sundry_type, p_posting_date, p_source,
                           v_transaction_id, p_session_id, p_central_date, p_audit_trail, p_user_id, p_mp_user_id);
      END IF;

      -- Update the case escalation if need be
      update_case_escalation(p_institution_number, p_case_number, v_new_escalated_flag, p_source,
                             v_transaction_id, p_session_id);

      -- Insert the action taken by the actor.
      v_params('INSTITUTION_NUMBER') := p_institution_number;
      v_params('SUNDRY_HISTORY_ID') := get_sequence_number(c_sundry_history_id_seq,
                                                                          p_institution_number);
      v_params('CASE_NUMBER') := p_case_number;
      v_params('RECORD_DATE') := p_posting_date;
      v_params('RECORD_TIME') := TO_CHAR(SYSDATE, c_time_format);
      v_params('RULE_ACTION') := p_action;
      v_params('NOTE_TEXT') := p_note_text;
      v_params('CENTRAL_DATE') := p_central_date;
      v_params('AUDIT_TRAIL') := p_audit_trail;
      v_params('ACQUIRER_REFERENCE') := v_acquirer_reference;
      v_params('CARD_ORGANIZATION') := v_card_organization;
      v_params('CLIENT_NUMBER') := v_client_number;
      v_params('SUNDRY_STATUS') := v_sundry_status;
      v_params('SUNDRY_TYPE') := v_new_sundry_type;
      v_params('USER_ID') := p_user_id;
      v_params('MP_USER_ID') := p_mp_user_id;
      v_params('RISK_ACCEPTED') := p_risk_accepted;
      v_params('PLANNED_REFUND_DATE') := p_action_date;

      -- Populate the chargeback reason if the current action is one of the following
      IF p_action IN (c_raise_pre_arbitration, c_pre_arbitration_rejected) THEN
        BEGIN
          SELECT ISH.CHARGEBACK_REASON
          INTO v_chargeback_reason
          FROM INT_SUNDRY_HISTORY ISH
          WHERE ISH.INSTITUTION_NUMBER = p_institution_number
          AND ISH.SUNDRY_HISTORY_ID = (SELECT MAX(SUNDRY_HISTORY_ID)
                                      FROM INT_SUNDRY_HISTORY
                                      WHERE INSTITUTION_NUMBER = ISH.INSTITUTION_NUMBER
                                      AND CASE_NUMBER = p_case_number
                                      AND SUNDRY_TYPE IN (c_first_chargeback,
                                                          c_second_chargeback)
                                      AND CHARGEBACK_REASON IS NOT NULL);
        EXCEPTION WHEN NO_DATA_FOUND THEN
          v_chargeback_reason := NULL;
        END;

        v_params('CHARGEBACK_REASON') := v_chargeback_reason;
      ELSE
        v_params('CHARGEBACK_REASON') := NULL;
      END IF;

      v_sql := 'INSERT INTO INT_SUNDRY_HISTORY (INSTITUTION_NUMBER, SUNDRY_HISTORY_ID, CASE_NUMBER, RECORD_DATE,
                                                RECORD_TIME, RULE_ACTION, CASE_STATUS, NOTE_TEXT, CENTRAL_DATE,
                                                ACQUIRER_REFERENCE, CARD_ORGANIZATION, CLIENT_NUMBER, SUNDRY_STATUS,
                                                SUNDRY_TYPE, CHARGEBACK_REASON, AUDIT_TRAIL, USER_ID, MP_USER_ID, RISK_ACCEPTED, PLANNED_REFUND_DATE)

                VALUES (:INSTITUTION_NUMBER, :SUNDRY_HISTORY_ID, :CASE_NUMBER, :RECORD_DATE, :RECORD_TIME, :RULE_ACTION,
                        :CASE_NEW_STATUS, :NOTE_TEXT, :CENTRAL_DATE, :ACQUIRER_REFERENCE, :CARD_ORGANIZATION, :CLIENT_NUMBER,
                        :SUNDRY_STATUS, :SUNDRY_TYPE, :CHARGEBACK_REASON, :AUDIT_TRAIL, :USER_ID, :MP_USER_ID, :RISK_ACCEPTED, :PLANNED_REFUND_DATE)';

      BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);

      IF v_revert_action = c_confirmation_yes
      THEN
        v_revert_success := enter_revert_record(row_revert_record_details,
                                                p_posting_date,
                                                p_current_language,
                                                p_default_language,
                                                p_central_date,
                                                p_source,
                                                p_session_id,
                                                p_user_id,
                                                p_mp_user_id,
                                                p_actor,
                                                v_transaction_id);
      END IF;
    END;

  PROCEDURE take_merchant_action(
    p_institution_number IN  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_client_number      IN  INT_TRANSACTIONS.CLIENT_NUMBER%TYPE,
    p_case_number        IN  INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_action             IN  SYS_SUNDRY_ACTION_SETUP.ACTION%TYPE,
    p_card_org           IN  SYS_SUNDRY_ACTION_SETUP.CARD_ORGANIZATION%TYPE,
    p_sundry_type        IN  SYS_SUNDRY_ACTION_SETUP.SUNDRY_TYPE%TYPE,
    p_note_text          IN  INT_SUNDRY_HISTORY.NOTE_TEXT%TYPE,
    p_documents          IN  VARCHAR2,
    p_fees               IN  VARCHAR2,
    p_current_language   IN  VARCHAR2,
    p_default_language   IN  VARCHAR2,
    p_chargeback_reason  IN  SYS_SUNDRY_ACTION_SETUP.CHARGEBACK_REASON_CODE%TYPE,
    p_document_location  IN  VARCHAR2,
    p_escalated_flag     IN  INT_SUNDRY_HISTORY.ESCALATED_FLAG%TYPE,
    p_case_status        IN  VARCHAR2 DEFAULT NULL,
    p_mp_user_id         IN  INT_SUNDRY_HISTORY.MP_USER_ID%TYPE,
    p_previous_action    IN SYS_SUNDRY_ACTION_SETUP.PREVIOUS_ACTION%TYPE,
    p_risk_accepted      IN INT_SUNDRY_HISTORY.RISK_ACCEPTED%TYPE DEFAULT NULL,
    p_action_date        IN VARCHAR2 DEFAULT NULL,
    o_return             OUT VARCHAR2
  )
  AS
    v_uploaded_docs BOOLEAN DEFAULT FALSE;
    v_days_to_action NUMBER;
    v_document_name INT_SUNDRY_HISTORY.DOCUMENT_NAME%TYPE;
    v_acquirer_reference INT_SUNDRY_HISTORY.ACQUIRER_REFERENCE%TYPE;
    v_central_date INT_SUNDRY_HISTORY.CENTRAL_DATE%TYPE;
    v_action_req    BWT_CONFIRMATION.INDEX_FIELD%TYPE;
  BEGIN

    o_return := '{ "documents" : {';

    FOR cur_rec IN (
    SELECT
      NAME,
      EXTENSION,
      DESCRIPTION,
      FILE_NAME,
      SUNDRY_HISTORY_ID
    FROM XMLTABLE('/documents/document'
                  PASSING XMLTYPE(p_documents)
                  COLUMNS
                    NAME VARCHAR2(55) PATH 'name',
                    EXTENSION VARCHAR2(5) PATH 'extension',
                    DESCRIPTION VARCHAR2(500) PATH 'description',
                    FILE_NAME VARCHAR2(50) PATH 'file_name',
                    SUNDRY_HISTORY_ID VARCHAR2(50) PATH 'sundry_history_id'
          ) xt)
    LOOP
      insert_document_details(p_institution_number => p_institution_number,
                              p_case_number  => p_case_number,
                              p_file_ext_id => cur_rec.EXTENSION,
                              p_note_text => cur_rec.DESCRIPTION,
                              p_descriptive_doc_name => cur_rec.name,
                              p_audit_trail => NULL,
                              p_user_id => p_mp_user_id,
                              p_document_location => p_document_location,
                              o_document_name => v_document_name,
                              p_sundry_history_id => cur_rec.SUNDRY_HISTORY_ID);

      o_return := o_return || '"' || cur_rec.FILE_NAME || '":"' || v_document_name || '.' || cur_rec.EXTENSION ||
                  '",';

      v_uploaded_docs := TRUE; --  true if at least one document was uploaded
    END LOOP;

    o_return := RTRIM(o_return, ',') || '} }';

    take_action(
                p_actor => c_merchant_actor,
                p_institution_number => p_institution_number,
                p_case_number => p_case_number,
                p_note_text => p_note_text,
                p_card_org => p_card_org,
                p_sundry_type => p_sundry_type,
                p_action => p_action,
                p_escalated_flag => p_escalated_flag,
                p_uploaded_docs => v_uploaded_docs,
                p_fees => p_fees,
                p_posting_date => TO_CHAR(SYSDATE, c_date_format),
                p_chargeback_reason => p_chargeback_reason,
                p_current_language => p_current_language,
                p_default_language => p_default_language,
                p_previous_action => p_previous_action,
                p_case_status => p_case_status,
                p_mp_user_id => p_mp_user_id,
                p_risk_accepted => p_risk_accepted,
                p_action_date => p_action_date);
  END;

  PROCEDURE take_operator_action(
    p_institution_number IN INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_number        IN INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_action             IN SYS_SUNDRY_ACTION_SETUP.ACTION%TYPE,
    p_previous_action    IN SYS_SUNDRY_ACTION_SETUP.PREVIOUS_ACTION%TYPE,
    p_card_org           IN SYS_SUNDRY_ACTION_SETUP.CARD_ORGANIZATION%TYPE,
    p_sundry_type        IN SYS_SUNDRY_ACTION_SETUP.SUNDRY_TYPE%TYPE,
    p_note_text          IN INT_SUNDRY_HISTORY.NOTE_TEXT%TYPE,
    p_amc_session_id     IN NUMBER,
    p_source             IN VARCHAR2,
    p_escalated_flag     IN INT_SUNDRY_HISTORY.ESCALATED_FLAG%TYPE,
    p_chargeback_reason  IN SYS_SUNDRY_ACTION_SETUP.CHARGEBACK_REASON_CODE%TYPE DEFAULT NULL,
    p_posting_date       IN SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format),
    p_current_language   IN VARCHAR2 DEFAULT 'USA',
    p_default_language   IN VARCHAR2 DEFAULT 'USA',
    p_central_date       IN INT_SUNDRY_HISTORY.CENTRAL_DATE%TYPE DEFAULT NULL,
    p_case_status        IN VARCHAR2 DEFAULT NULL,
    p_uploaded_docs      IN BWT_CONFIRMATION.INDEX_FIELD%TYPE DEFAULT NULL,
    p_audit_trail        IN INT_SUNDRY_HISTORY.AUDIT_TRAIL%TYPE,
    p_user_id            IN INT_SUNDRY_HISTORY.USER_ID%TYPE,
    p_result             OUT VARCHAR2
  )
  AS
    v_uploaded_docs BOOLEAN := FALSE;
    v_user_id INT_SUNDRY_HISTORY.USER_ID%TYPE;

    BEGIN
      p_result := NULL;
      IF p_source IS NOT NULL AND p_source <> 'acq_open_case.prc'
      THEN
        SELECT USER_ID
        INTO v_user_id
        FROM TABLE(get_case_header_details(p_institution_number, p_case_number));

        IF v_user_id != p_user_id
        THEN
          RAISE_APPLICATION_ERROR(-20001, 'User '''|| p_user_id || ''' cannot take an action on case ''' || p_case_number || '''');
        END IF;
      END IF;

      IF p_uploaded_docs = c_confirmation_yes
      THEN
        v_uploaded_docs := TRUE;
      END IF;

      BEGIN
        take_action(
                    p_actor => c_operator_actor,
                    p_institution_number => p_institution_number,
                    p_case_number => p_case_number,
                    p_note_text => p_note_text,
                    p_card_org => p_card_org,
                    p_sundry_type => p_sundry_type,
                    p_action => p_action,
                    p_escalated_flag => p_escalated_flag,
                    p_uploaded_docs => v_uploaded_docs,
                    p_posting_date => p_posting_date,
                    p_chargeback_reason => p_chargeback_reason,
                    p_current_language => p_current_language,
                    p_default_language => p_default_language,
                    p_case_status => p_case_status,
                    p_session_id => p_amc_session_id,
                    p_source => p_source,
                    p_previous_action => p_previous_action,
                    p_central_date => p_central_date,
                    p_audit_trail => p_audit_trail,
                    p_user_id => p_user_id);
      EXCEPTION WHEN action_not_found THEN
        p_result := 'action-not-found';
      WHEN revert_not_allowed THEN
        p_result := 'revert-not-allowed';
      WHEN already_sent_to_scheme THEN
        p_result := 'already-sent-to-scheme';
      END;
    END;

PROCEDURE set_seen_by_merchant(
    p_institution_number   IN   INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_sundry_history_id    IN   INT_SUNDRY_HISTORY.SUNDRY_HISTORY_ID%TYPE,
    o_success              OUT  BWT_CONFIRMATION.INDEX_FIELD%TYPE
  )
  AS
    v_params              BW_DISPUTES_CORE.ASSOC_ARRAY;
    v_sql                 VARCHAR2(300);
    v_sundry_history_id   INT_SUNDRY_HISTORY.SUNDRY_HISTORY_ID%TYPE;

  BEGIN
    o_success := c_confirmation_yes;
    v_params('INSTITUTION_NUMBER') := p_institution_number;
    v_params('SUNDRY_HISTORY_ID') := p_sundry_history_id;
    v_params('CONFIRMATION_YES') := c_confirmation_yes;

    SELECT SUNDRY_HISTORY_ID
    INTO v_sundry_history_id
    FROM INT_SUNDRY_HISTORY
    WHERE INSTITUTION_NUMBER = p_institution_number
    AND SUNDRY_HISTORY_ID = p_sundry_history_id;

    v_sql := 'UPDATE INT_SUNDRY_HISTORY
              SET SEEN_BY_MERCHANT = :CONFIRMATION_YES
              WHERE INSTITUTION_NUMBER = :INSTITUTION_NUMBER
              AND SUNDRY_HISTORY_ID = :SUNDRY_HISTORY_ID';

    BW_DISPUTES_CORE.execute_statement(v_sql, v_params, null, null, null);

  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      o_success := c_confirmation_no;
  END;


  PROCEDURE take_support_action(
    p_institution_number IN INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_number        IN INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_action             IN SYS_SUNDRY_ACTION_SETUP.ACTION%TYPE,
    p_previous_action    IN SYS_SUNDRY_ACTION_SETUP.PREVIOUS_ACTION%TYPE,
    p_card_org           IN SYS_SUNDRY_ACTION_SETUP.CARD_ORGANIZATION%TYPE,
    p_sundry_type        IN SYS_SUNDRY_ACTION_SETUP.SUNDRY_TYPE%TYPE,
    p_note_text          IN INT_SUNDRY_HISTORY.NOTE_TEXT%TYPE,
    p_amc_session_id     IN NUMBER,
    p_source             IN VARCHAR2,
    p_escalated_flag     IN INT_SUNDRY_HISTORY.ESCALATED_FLAG%TYPE,
    p_chargeback_reason  IN SYS_SUNDRY_ACTION_SETUP.CHARGEBACK_REASON_CODE%TYPE DEFAULT NULL,
    p_posting_date       IN SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format),
    p_current_language   IN VARCHAR2 DEFAULT 'USA',
    p_default_language   IN VARCHAR2 DEFAULT 'USA',
    p_central_date       IN INT_SUNDRY_HISTORY.CENTRAL_DATE%TYPE DEFAULT NULL,
    p_case_status        IN VARCHAR2 DEFAULT NULL,
    p_uploaded_docs      IN BWT_CONFIRMATION.INDEX_FIELD%TYPE DEFAULT NULL,
    p_audit_trail        IN INT_SUNDRY_HISTORY.AUDIT_TRAIL%TYPE,
    p_user_id            IN INT_SUNDRY_HISTORY.USER_ID%TYPE,
    p_result             OUT VARCHAR2
  )
  AS
    v_uploaded_docs BOOLEAN := FALSE;
    BEGIN
      p_result := NULL;
      IF p_uploaded_docs = c_confirmation_yes
      THEN
        v_uploaded_docs := TRUE;
      END IF;

      BEGIN
        take_action(
                    p_actor => c_support_actor,
                    p_institution_number => p_institution_number,
                    p_case_number => p_case_number,
                    p_note_text => p_note_text,
                    p_card_org => p_card_org,
                    p_sundry_type => p_sundry_type,
                    p_action => p_action,
                    p_escalated_flag => p_escalated_flag,
                    p_uploaded_docs => v_uploaded_docs,
                    p_posting_date => p_posting_date,
                    p_chargeback_reason => p_chargeback_reason,
                    p_current_language => p_current_language,
                    p_default_language => p_default_language,
                    p_case_status => p_case_status,
                    p_session_id => p_amc_session_id,
                    p_source => p_source,
                    p_previous_action => p_previous_action,
                    p_central_date => p_central_date,
                    p_audit_trail => p_audit_trail,
                    p_user_id => p_user_id);
      EXCEPTION WHEN action_not_found THEN
        p_result := 'action-not-found';
      WHEN revert_not_allowed THEN
        p_result := 'revert-not-allowed';
      WHEN already_sent_to_scheme THEN
        p_result := 'already-sent-to-scheme';
      END;
    END;

  PROCEDURE unshare_documents(
    p_institution_number  IN INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_number         IN INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_sundry_history_ids  IN BW_DISPUTES_ARRAY,
    p_source              IN GUI_MASK_MENU.MASK_NAME%TYPE DEFAULT NULL,
    p_session_id          IN NUMBER DEFAULT NULL
  )
  AS
    v_params              BW_DISPUTES_CORE.ASSOC_ARRAY;
    v_sundry_history_id   VARCHAR2(11);
    v_sql                 VARCHAR2(4000);
    v_transaction_id      NUMBER;

    BEGIN
      IF p_session_id IS NOT NULL THEN
        BW_AMC.AMC_AUDIT.InitTransaction(v_transaction_id);
      END IF;

      FOR i IN 1 .. p_sundry_history_ids.count LOOP
        v_sundry_history_id := p_sundry_history_ids(i);

        v_params('INSTITUTION_NUMBER') := p_institution_number;
        v_params('CASE_NUMBER') := p_case_number;
        v_params('MERCHANT_VISIBLE') := c_confirmation_no;
        v_params('SUNDRY_HISTORY_ID') := v_sundry_history_id;

        v_sql := 'UPDATE INT_SUNDRY_HISTORY
                    SET MERCHANT_VISIBLE = :MERCHANT_VISIBLE
                    WHERE
                        INSTITUTION_NUMBER = :INSTITUTION_NUMBER
                        AND SUNDRY_HISTORY_ID = :SUNDRY_HISTORY_ID
                        AND CASE_NUMBER = :CASE_NUMBER';

        BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);
      END LOOP;
    END;

  PROCEDURE acknowledge_messages(
    p_institution_number IN INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_number        IN INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE DEFAULT NULL,
    p_is_portal_request  IN VARCHAR2 DEFAULT 'FALSE',
    p_sundry_history_id  IN INT_SUNDRY_HISTORY.SUNDRY_HISTORY_ID%TYPE DEFAULT NULL,
    p_source             IN GUI_MASK_MENU.MASK_NAME%TYPE DEFAULT NULL,
    p_session_id         IN NUMBER DEFAULT NULL,
    o_success            OUT BWT_CONFIRMATION.INDEX_FIELD%TYPE
  )
  AS
    v_transaction_id  NUMBER;
    v_params          BW_DISPUTES_CORE.ASSOC_ARRAY;
    v_sql             VARCHAR2(4000);
  BEGIN
    IF p_session_id IS NOT NULL THEN
      BW_AMC.AMC_AUDIT.InitTransaction(v_transaction_id);
    END IF;

    -- If sundry history ID was passed then go to single record mode,
    -- otherwise go to all records mode.
    IF p_sundry_history_id IS NOT NULL THEN
      v_sql := 'UPDATE INT_SUNDRY_HISTORY
                SET ACKNOWLEDGED_FLAG = :CONFIRMATION_YES
                WHERE INSTITUTION_NUMBER = :INSTITUTION_NUMBER
                AND SUNDRY_HISTORY_ID = :SUNDRY_HISTORY_ID';

      v_params('CONFIRMATION_YES') := c_confirmation_yes;
      v_params('INSTITUTION_NUMBER') := p_institution_number;
      v_params('SUNDRY_HISTORY_ID') := p_sundry_history_id;

    ELSIF p_case_number IS NOT NULL THEN
      v_sql := 'UPDATE INT_SUNDRY_HISTORY
                SET ACKNOWLEDGED_FLAG = :CONFIRMATION_YES
                WHERE INSTITUTION_NUMBER = :INSTITUTION_NUMBER
                AND CASE_NUMBER = :CASE_NUMBER ';

      IF p_is_portal_request = 'FALSE' THEN -- Operator
        v_sql := v_sql || ' AND MP_USER_ID IS NOT NULL ';
      ELSIF p_is_portal_request = 'TRUE' THEN-- Merchant
        v_sql := v_sql || ' AND USER_ID IS NOT NULL ';
      END IF;

      v_sql := v_sql || ' AND NOTE_TEXT IS NOT NULL
                AND NVL(ACKNOWLEDGED_FLAG, :CONFIRMATION_NO) <> :CONFIRMATION_YES';

      v_params('CONFIRMATION_YES') := c_confirmation_yes;
      v_params('CONFIRMATION_NO') := c_confirmation_no;
      v_params('INSTITUTION_NUMBER') := p_institution_number;
      v_params('CASE_NUMBER') := p_case_number;
    ELSE
      o_success := c_confirmation_no;
      RAISE_APPLICATION_ERROR(-20001, 'Minimum required parameters where not passed to acknowledge_messages(..)');
    END IF;

    BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);
    o_success := c_confirmation_yes;
  END;

  PROCEDURE record_letter_generation(
    p_institution_number   IN INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_number          IN BW_DISPUTES_ARRAY,
    p_file_ext_id          IN BW_DISPUTES_ARRAY,
    p_descriptive_doc_name IN BW_DISPUTES_ARRAY,
    p_document_location    IN BW_DISPUTES_ARRAY,
    p_user_id              IN INT_SUNDRY_HISTORY.USER_ID%TYPE,
    p_posting_date         IN SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format),
    p_source               IN GUI_MASK_MENU.MASK_NAME%TYPE DEFAULT NULL,
    p_session_id           IN NUMBER DEFAULT NULL,
    p_transaction_id       IN NUMBER DEFAULT NULL,
    o_success              OUT BOOLEAN
  )
  AS
    v_params                BW_DISPUTES_CORE.ASSOC_ARRAY;
    v_case_number           INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE;
    v_audit_trail           INT_SUNDRY_HISTORY.DOCUMENT_LOCATION%TYPE;
    v_acquirer_reference    INT_SUNDRY_HISTORY.ACQUIRER_REFERENCE%TYPE;
    v_case_status           INT_SUNDRY_HISTORY.CASE_STATUS%TYPE;
    v_card_organization     INT_SUNDRY_HISTORY.CARD_ORGANIZATION%TYPE;
    v_client_number         INT_SUNDRY_HISTORY.CLIENT_NUMBER%TYPE;
    v_document_name         INT_SUNDRY_HISTORY.DOCUMENT_NAME%TYPE;
    v_sql                   VARCHAR2(4000);
  BEGIN
    o_success := true;
    FOR i in 1 .. p_case_number.count LOOP
      v_case_number := p_case_number(i);
      v_audit_trail := (BW_CODE_LIBRARY.CreateAuditTrail(p_user_id, c_station_number));

      -- insert the 'attach document' (rule_action 106) record
      insert_operator_document_details(
                                        p_institution_number => p_institution_number,
                                        p_case_number => v_case_number,
                                        p_file_ext_id => p_file_ext_id(i),
                                        p_descriptive_doc_name => p_descriptive_doc_name(i),
                                        p_audit_trail => v_audit_trail,
                                        p_user_id => p_user_id,
                                        p_document_location => p_document_location(i),
                                        o_document_name => v_document_name,
                                        p_posting_date => p_posting_date);

      IF v_document_name IS NULL THEN
        o_success := false;
        EXIT;
      END IF;

      -- Get details from case header
      SELECT ACQUIRER_REFERENCE, CARD_ORGANIZATION, CLIENT_NUMBER, CASE_STATUS
      INTO v_acquirer_reference, v_card_organization, v_client_number, v_case_status
      FROM TABLE(get_case_header_details(p_institution_number, v_case_number));

      -- insert the 'system generated document' record.
      v_params('INSTITUTION_NUMBER') := p_institution_number;
      v_params('CASE_NUMBER') := v_case_number;
      v_params('SUNDRY_HISTORY_ID') := get_sequence_number(c_sundry_history_id_seq, p_institution_number);
      v_params('RULE_ACTION') := c_system_generated_document;
      v_params('SUNDRY_STATUS') := c_approved_status;
      v_params('SUNDRY_TYPE') := get_last_sundry_type(v_case_number, p_institution_number);
      v_params('USER_ID') := p_user_id;
      v_params('NOTE_TEXT') := 'Bank auto-generated letter';
      v_params('RECORD_DATE') := p_posting_date;
      v_params('RECORD_TIME') := TO_CHAR(SYSDATE, c_time_format);
      v_params('ACQUIRER_REFERENCE') := v_acquirer_reference;
      v_params('AUDIT_TRAIL') := (BW_CODE_LIBRARY.CreateAuditTrail(p_user_id, c_station_number));
      v_params('CARD_ORGANIZATION') := v_card_organization;
      v_params('CASE_STATUS') := v_case_status;
      v_params('CLIENT_NUMBER') := v_client_number;

      v_sql := 'INSERT INTO INT_SUNDRY_HISTORY (INSTITUTION_NUMBER, CASE_NUMBER, SUNDRY_HISTORY_ID, RULE_ACTION,
                                                SUNDRY_STATUS, SUNDRY_TYPE, USER_ID, NOTE_TEXT, RECORD_DATE,
                                                RECORD_TIME, ACQUIRER_REFERENCE, AUDIT_TRAIL, CARD_ORGANIZATION,
                                                CASE_STATUS, CLIENT_NUMBER)

                VALUES (:INSTITUTION_NUMBER, :CASE_NUMBER,  :SUNDRY_HISTORY_ID, :RULE_ACTION, :SUNDRY_STATUS, :SUNDRY_TYPE,
                        :USER_ID, :NOTE_TEXT, :RECORD_DATE, :RECORD_TIME, :ACQUIRER_REFERENCE, :AUDIT_TRAIL, :CARD_ORGANIZATION,
                        :CASE_STATUS, :CLIENT_NUMBER)';

        BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, p_transaction_id);
    END LOOP;
  END;

  PROCEDURE insert_document_upload_details(
    p_institution_number    IN  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_numbers          IN  BW_DISPUTES_ARRAY,
    p_file_ext_ids          IN  BW_DISPUTES_ARRAY,
    p_descriptive_doc_names IN  BW_DISPUTES_ARRAY,
    p_document_locations    IN  BW_DISPUTES_ARRAY,
    p_posting_date          IN  SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format),
    p_user_id               IN  INT_SUNDRY_HISTORY.USER_ID%TYPE DEFAULT NULL,
    p_mp_user_id            IN  INT_SUNDRY_HISTORY.MP_USER_ID%TYPE DEFAULT NULL,
    p_source                IN  GUI_MASK_MENU.MASK_NAME%TYPE DEFAULT NULL,
    p_session_id            IN  NUMBER DEFAULT NULL,
    p_transaction_id        IN  NUMBER DEFAULT NULL,
    o_success               OUT BWT_CONFIRMATION.INDEX_FIELD%TYPE,
    p_sundry_history_id     IN  INT_SUNDRY_HISTORY.SUNDRY_HISTORY_ID%TYPE DEFAULT NULL
  )
  AS
    o_document_name     INT_SUNDRY_HISTORY.DOCUMENT_NAME%TYPE;
    v_result            BWT_CONFIRMATION.INDEX_FIELD%TYPE := c_confirmation_yes;
    v_audit_trail       INT_SUNDRY_HISTORY.AUDIT_TRAIL%TYPE DEFAULT NULL;
  BEGIN
    FOR i IN 1 .. p_case_numbers.count LOOP
      v_audit_trail := bw_code_library.createAuditTrail(p_user_id, c_station_number);
      link_document_to_case(p_institution_number => p_institution_number,
                            p_case_number => p_case_numbers(i),
                            p_file_ext_id => p_file_ext_ids(i),
                            p_note_text => '',
                            p_descriptive_doc_name => p_descriptive_doc_names(i),
                            p_rule_action => c_add_document_to_case_storage,
                            p_audit_trail => v_audit_trail,
                            p_user_id => p_user_id,
                            p_mp_user_id => p_mp_user_id,
                            p_document_location => p_document_locations(i),
                            o_document_name => o_document_name,
                            p_source => p_source,
                            p_session_id => p_session_id,
                            p_transaction_id => p_transaction_id,
                            p_sundry_history_id => p_sundry_history_id,
                            p_posting_date => p_posting_date
                          );

      IF o_document_name IS NULL THEN
        v_result := c_confirmation_no;
      END IF;
    END LOOP;
    o_success := v_result;
  END;

  PROCEDURE insert_bulk_operator_documents(
    p_institution_number    IN  INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_numbers          IN  BW_DISPUTES_ARRAY,
    p_file_ext_ids          IN  BW_DISPUTES_ARRAY,
    p_descriptive_doc_names IN  BW_DISPUTES_ARRAY,
    p_document_locations    IN  BW_DISPUTES_ARRAY,
    p_posting_date          IN  SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format),
    p_user_id               IN  INT_SUNDRY_HISTORY.USER_ID%TYPE DEFAULT NULL,
    p_source                IN  GUI_MASK_MENU.MASK_NAME%TYPE DEFAULT NULL,
    p_session_id            IN  NUMBER DEFAULT NULL,
    p_transaction_id        IN  NUMBER DEFAULT NULL,
    o_success               OUT BWT_CONFIRMATION.INDEX_FIELD%TYPE
  )
  AS
    o_document_name     INT_SUNDRY_HISTORY.DOCUMENT_NAME%TYPE;
    v_result            BWT_CONFIRMATION.INDEX_FIELD%TYPE := c_confirmation_yes;
    v_audit_trail       INT_SUNDRY_HISTORY.AUDIT_TRAIL%TYPE DEFAULT NULL;
  BEGIN
    FOR i IN 1 .. p_case_numbers.count LOOP
      v_audit_trail := bw_code_library.createAuditTrail(p_user_id, c_station_number);

      insert_operator_document_details(
                                        p_institution_number => p_institution_number,
                                        p_case_number => p_case_numbers(i),
                                        p_file_ext_id => p_file_ext_ids(i),
                                        p_descriptive_doc_name => p_descriptive_doc_names(i),
                                        p_audit_trail => v_audit_trail,
                                        p_user_id => p_user_id,
                                        p_document_location => p_document_locations(i),
                                        o_document_name => o_document_name,
                                        p_source => p_source,
                                        p_session_id => p_session_id,
                                        p_transaction_id => p_transaction_id,
                                        p_sundry_history_id => '',
                                        p_posting_date => p_posting_date
                                      );
      IF o_document_name IS NULL THEN
        v_result := c_confirmation_no;
      END IF;
    END LOOP;
    o_success := v_result;
  END;

  PROCEDURE transfer_case(
    p_institution_number  IN INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_numbers        IN BW_DISPUTES_ARRAY,
    p_triggering_user_id  IN INT_SUNDRY_HISTORY.USER_ID%TYPE,
    p_destination_user_id IN INT_SUNDRY_HISTORY.USER_ID%TYPE DEFAULT NULL,
    p_action_id           IN INT_SUNDRY_HISTORY.RULE_ACTION%TYPE DEFAULT c_transfer_to_new_user,
    p_posting_date        IN SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format),
    p_source              IN GUI_MASK_MENU.MASK_NAME%TYPE DEFAULT NULL,
    p_session_id          IN NUMBER DEFAULT NULL,
    p_transaction_id      IN NUMBER DEFAULT NULL,
    o_successful          OUT BWT_CONFIRMATION.INDEX_FIELD%TYPE
  )
  AS
    v_params              BW_DISPUTES_CORE.ASSOC_ARRAY;
    v_case_number         VARCHAR2(11);
    v_sql                 VARCHAR2(4000);
    v_acquirer_reference  INT_SUNDRY_HISTORY.ACQUIRER_REFERENCE%TYPE;
    v_card_organization   INT_SUNDRY_HISTORY.CARD_ORGANIZATION%TYPE;
    v_client_number       INT_SUNDRY_HISTORY.CLIENT_NUMBER%TYPE;
    v_sundry_status       INT_SUNDRY_HISTORY.SUNDRY_STATUS%TYPE;
    v_case_status         INT_SUNDRY_HISTORY.CASE_STATUS%TYPE;
    v_transaction_id      NUMBER;
    v_owner_user_id       INT_SUNDRY_HISTORY.USER_ID%TYPE;

    BEGIN
      o_successful := '001';

      IF p_session_id IS NOT NULL THEN
        IF p_transaction_id IS NULL THEN
          BW_AMC.AMC_AUDIT.InitTransaction(v_transaction_id);
        ELSE
          v_transaction_id := p_transaction_id;
        END IF;
      END IF;

      BEGIN -- Exception block
        FOR i IN 1 .. p_case_numbers.count LOOP
          -- Update the case header with user id
          v_case_number := p_case_numbers(i);
          BEGIN
            -- Retrieving the case header details and make sure the case exists
            SELECT ACQUIRER_REFERENCE, CARD_ORGANIZATION, CLIENT_NUMBER, CASE_STATUS
            INTO v_acquirer_reference, v_card_organization, v_client_number, v_case_status
            FROM TABLE(get_case_header_details(p_institution_number, v_case_number));

          EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20001, 'No data related to the case was found');
          END;

          v_params('INSTITUTION_NUMBER') := p_institution_number;
          v_params('CASE_NUMBER') := v_case_number;
          v_params('RULE_ACTION') := c_case_header_action;
          v_params('USER_ID') := p_destination_user_id;  
          
                         
          --Locking the case header record so no other simultaneous requests overwrite this change.        
          SELECT USER_ID 
          INTO v_owner_user_id 
          FROM INT_SUNDRY_HISTORY 
          WHERE RULE_ACTION = c_case_header_action
          AND INSTITUTION_NUMBER = p_institution_number
          AND CASE_NUMBER = v_case_number
          FOR UPDATE OF USER_ID;
          
          IF v_owner_user_id IS NOT NULL and p_action_id = c_locked_case_next_action THEN 
            RAISE_APPLICATION_ERROR(-20005, 'Case is already assigned, cannot be locked.');
          END IF;        
          
          v_sql := 'UPDATE INT_SUNDRY_HISTORY
                      SET USER_ID = :USER_ID
                      WHERE
                          RULE_ACTION = :RULE_ACTION
                          AND INSTITUTION_NUMBER = :INSTITUTION_NUMBER
                          AND CASE_NUMBER = :CASE_NUMBER';

          BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);

          v_params('ACQUIRER_REFERENCE') := v_acquirer_reference;
          v_params('CARD_ORGANIZATION') := v_card_organization;
          v_params('CLIENT_NUMBER') := v_client_number;
          v_params('CASE_NUMBER') := v_case_number;
          v_params('CASE_STATUS') := v_case_status;
          v_params('INSTITUTION_NUMBER') := p_institution_number;
          v_params('AUDIT_TRAIL') := (BW_CODE_LIBRARY.CreateAuditTrail(p_triggering_user_id, c_station_number));
          v_params('RECORD_DATE') := p_posting_date;
          v_params('RECORD_TIME') := TO_CHAR(SYSDATE, c_time_format);
          v_params('RULE_ACTION') := p_action_id;
          v_params('TRIGGER_USER_ID') := p_triggering_user_id;
          v_params('NOTE_TEXT') := '';
          v_params('SUNDRY_STATUS') := c_approved_status;
          v_params('SUNDRY_HISTORY_ID') := get_sequence_number(c_sundry_history_id_seq, 
                                            p_institution_number);
          v_params('SUNDRY_TYPE') := get_last_sundry_type(v_case_number, p_institution_number);

          IF p_action_id = c_transfer_to_new_user
          THEN
            IF p_destination_user_id IS NOT NULL
            THEN
              v_params('NOTE_TEXT') := 'Case Transferred To ' || p_destination_user_id;
            ELSE
              v_params('NOTE_TEXT') := 'Case was unassigned';
            END IF;
          END IF;

          -- Inserting new record in int_sundry_history
          v_sql := 'INSERT INTO INT_SUNDRY_HISTORY (INSTITUTION_NUMBER, SUNDRY_HISTORY_ID, CASE_NUMBER, RECORD_DATE,
                                                    RECORD_TIME, RULE_ACTION, SUNDRY_TYPE, ACQUIRER_REFERENCE,
                                                    CARD_ORGANIZATION, CLIENT_NUMBER, SUNDRY_STATUS, CASE_STATUS, AUDIT_TRAIL,
                                                    USER_ID, NOTE_TEXT)

                    VALUES (:INSTITUTION_NUMBER, :SUNDRY_HISTORY_ID, :CASE_NUMBER, :RECORD_DATE,
                            :RECORD_TIME, :RULE_ACTION, :SUNDRY_TYPE, :ACQUIRER_REFERENCE,
                            :CARD_ORGANIZATION, :CLIENT_NUMBER, :SUNDRY_STATUS, :CASE_STATUS, :AUDIT_TRAIL,
                            :TRIGGER_USER_ID, :NOTE_TEXT)';

          BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);

        END LOOP;  

      EXCEPTION
        WHEN already_assigned_case THEN
          o_successful := '005'; --Assigned 
        WHEN OTHERS THEN
          o_successful := '000'; --Not Successful 
      END;
    END;

  FUNCTION get_configured_queues(p_institution_number BWT_DISPUTE_QUEUE_ID.INSTITUTION_NUMBER%TYPE,
                                 p_current_language   BWT_DISPUTE_QUEUE_ID.LANGUAGE%TYPE DEFAULT 'USA',
                                 p_default_language   BWT_DISPUTE_QUEUE_ID.LANGUAGE%TYPE DEFAULT 'USA')
    RETURN TBL_CONFIGURED_DISPUTE_QUEUES
    PIPELINED AS
      CURSOR tbl_cursor IS
        SELECT QID.INDEX_FIELD, QID.DISPUTE_QUEUE
        FROM SYS_DISPUTE_QUEUES Q,
            BWT_DISPUTE_QUEUE_ID QID
        WHERE Q.INSTITUTION_NUMBER = p_institution_number
        AND QID.INSTITUTION_NUMBER = Q.INSTITUTION_NUMBER
        AND QID.INDEX_FIELD = Q.DISPUTE_QUEUE_ID
        AND QID.LANGUAGE = CASE WHEN EXISTS(SELECT INDEX_FIELD
                                             FROM BWT_DISPUTE_QUEUE_ID
                                             WHERE LANGUAGE = p_current_language
                                             AND INSTITUTION_NUMBER = QID.INSTITUTION_NUMBER
                                             AND INDEX_FIELD = QID.INDEX_FIELD)
                          THEN p_current_language
                          ELSE p_default_language
                          END;
  BEGIN
    FOR cursor_record IN tbl_cursor LOOP
      PIPE ROW (cursor_record);
    END LOOP;
  END get_configured_queues;

  FUNCTION get_queue_ordering(p_institution_number SYS_DISPUTE_QUEUES.INSTITUTION_NUMBER%TYPE,
                              p_queue_id           SYS_DISPUTE_QUEUES.DISPUTE_QUEUE_ID%TYPE)
    RETURN SYS_DISPUTE_QUEUES.ORDER_BY%TYPE AS
      v_order_by  SYS_DISPUTE_QUEUES.ORDER_BY%TYPE;

  BEGIN
    SELECT Q.ORDER_BY
    INTO v_order_by
    FROM SYS_DISPUTE_QUEUES Q
    WHERE Q.INSTITUTION_NUMBER = p_institution_number
    AND Q.DISPUTE_QUEUE_ID = p_queue_id;

    RETURN v_order_by;
  END get_queue_ordering;

  FUNCTION get_revert_action_details(
    p_institution_number    INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
    p_case_number           INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE,
    p_actor                 BWT_SUNDRY_ACTOR.INDEX_FIELD%TYPE )

    RETURN TBL_REVERT_ACTION_DETAILS
    PIPELINED AS
      v_last_action ROW_LAST_ACTOR_ACTION;

      CURSOR tbl_cursor (cp_last_actor_action_id INT_SUNDRY_HISTORY.SUNDRY_HISTORY_ID%TYPE) IS
        SELECT RULE_ACTION, RECORD_DATE, RECORD_TIME, USR.USERNAME
        FROM INT_SUNDRY_HISTORY HST,
            SYS_USER_INFORMATION USR
        WHERE INSTITUTION_NUMBER = p_institution_number
        AND SUNDRY_HISTORY_ID = cp_last_actor_action_id
        AND USR.USERID = HST.USER_ID;
    BEGIN
      v_last_action := get_last_action_by_actor(p_institution_number => p_institution_number,
                                                p_case_number => p_case_number,
                                                p_actor => p_actor);

      FOR cursor_record IN tbl_cursor (v_last_action.SUNDRY_HISTORY_ID) LOOP
        PIPE ROW (cursor_record);
      END LOOP;
    END get_revert_action_details;

  PROCEDURE update_queue_settings(
    p_institution_number  IN  SYS_DISPUTE_QUEUES.INSTITUTION_NUMBER%TYPE,
    p_queue_id            IN  SYS_DISPUTE_QUEUES.DISPUTE_QUEUE_ID%TYPE,
    p_order_by            IN  SYS_DISPUTE_QUEUES.ORDER_BY%TYPE,
    p_source              IN  GUI_MASK_MENU.MASK_NAME%TYPE,
    p_session_id          IN  NUMBER,   --required
    o_success             OUT BWT_CONFIRMATION.INDEX_FIELD%TYPE
  ) AS

    v_sql                 VARCHAR2(4000);
    v_params              BW_DISPUTES_CORE.ASSOC_ARRAY;
    v_transaction_id      NUMBER;
    v_count         NUMBER;
  BEGIN

    BEGIN
      SELECT count(1)
      INTO v_count
      FROM SYS_DISPUTE_QUEUES Q
      WHERE Q.INSTITUTION_NUMBER = p_institution_number
      AND Q.DISPUTE_QUEUE_ID = p_queue_id;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      o_success := c_confirmation_no;
    END;

    o_success := c_confirmation_yes;

    BW_AMC.AMC_AUDIT.InitTransaction(v_transaction_id);

    v_params('INSTITUTION_NUMBER') := p_institution_number;
    v_params('ORDER_BY') := p_order_by;
    v_params('DISPUTE_QUEUE_ID') := p_queue_id;

    v_sql := 'UPDATE SYS_DISPUTE_QUEUES
            SET ORDER_BY = :ORDER_BY
            WHERE INSTITUTION_NUMBER = :INSTITUTION_NUMBER
            AND DISPUTE_QUEUE_ID = :DISPUTE_QUEUE_ID';

    BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);

  END update_queue_settings;

  FUNCTION get_user_settings(
    p_institution_number  SYS_DISPUTE_QUEUES.INSTITUTION_NUMBER%TYPE,
    p_user_id             SYS_USER_INFORMATION.USERID%TYPE,
    p_posting_date        SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format)
  ) RETURN CLOB
  AS
    v_user_settings CLOB;

  BEGIN
    SELECT JSON_OBJECT('USER_ID' VALUE TEAM.USER_ID,
                      'TEAM_ID' VALUE TEAM.TEAM_ID,
                      'CURRENCY' VALUE TEAM.RANGE_CURRENCY,
                      'AMOUNT_FROM' VALUE TEAM.RANGE_START,
                      'AMOUNT_TO' VALUE TEAM.RANGE_END,
                      'CARD_ORGANIZATIONS' VALUE (SELECT JSON_ARRAYAGG(JSON_OBJECT('CARD_ORGANIZATION' VALUE PROC.CARD_ORGANIZATION))
                                                  FROM SYS_SUNDRY_USER_PROCESS PROC
                                                  WHERE PROC.INSTITUTION_NUMBER = TEAM.INSTITUTION_NUMBER
                                                  AND PROC.USER_ID = TEAM.USER_ID),
                      'QUEUES' VALUE (SELECT JSON_ARRAYAGG(JSON_OBJECT('QUEUE' VALUE QMAP.DISPUTE_QUEUE_ID,
                                                                       'PRIORITY' VALUE QMAP.PRIORITY))
                                      FROM SYS_DISPUTE_QUEUE_MAPPING QMAP
                                      WHERE QMAP.INSTITUTION_NUMBER = TEAM.INSTITUTION_NUMBER
                                      AND QMAP.USER_ID = TEAM.USER_ID
                                      GROUP BY QMAP.USER_ID),
                      'RANGE_ID' VALUE TEAM.RANGE_ID
                      ) AS USER_SETTINGS
    INTO v_user_settings
    FROM SYS_SUNDRY_TEAM_ASSIGN TEAM
    WHERE TEAM.INSTITUTION_NUMBER = p_institution_number
    AND TEAM.USER_ID = p_user_id
    AND TEAM.EFFECTIVE_DATE = (SELECT MAX(EFFECTIVE_DATE)
                                FROM SYS_SUNDRY_TEAM_ASSIGN
                                WHERE INSTITUTION_NUMBER = TEAM.INSTITUTION_NUMBER
                                AND USER_ID = TEAM.USER_ID
                                AND EFFECTIVE_DATE <= p_posting_date)
    GROUP BY TEAM.INSTITUTION_NUMBER,
            TEAM.USER_ID,
            TEAM.TEAM_ID,
            TEAM.RANGE_CURRENCY,
            TEAM.RANGE_START,
            TEAM.RANGE_END,
            TEAM.RANGE_ID;

    RETURN v_user_settings;

  END get_user_settings;

  PROCEDURE save_user_settings(
    p_institution_number  IN  SYS_DISPUTE_QUEUES.INSTITUTION_NUMBER%TYPE,
    p_user_settings       IN  CLOB,
    p_posting_date        IN  SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format),
    p_source              IN  GUI_MASK_MENU.MASK_NAME%TYPE,
    p_session_id          IN  NUMBER DEFAULT NULL,
    o_success             OUT VARCHAR2
    ) AS
      v_setting_obj       JSON_OBJECT_T;
      card_org_obj        JSON_OBJECT_T;
      card_org_array      JSON_ARRAY_T;
      queue_obj           JSON_OBJECT_T;
      queue_array         JSON_ARRAY_T;
      v_user_id           SYS_SUNDRY_TEAM_ASSIGN.USER_ID%TYPE;
      v_card_org          SYS_SUNDRY_USER_PROCESS.CARD_ORGANIZATION%TYPE;
      v_queue_id          SYS_DISPUTE_QUEUE_MAPPING.DISPUTE_QUEUE_ID%TYPE;
      v_queue_priority    SYS_DISPUTE_QUEUE_MAPPING.PRIORITY%TYPE;
      v_params            BW_DISPUTES_CORE.ASSOC_ARRAY;
      v_sql               VARCHAR2(4000);
      v_transaction_id    NUMBER;
      v_count             NUMBER;
      v_range_id          VARCHAR2(10);
      v_amount_from       VARCHAR2(20);
      v_amount_to         VARCHAR2(20);
      v_is_valid          BOOLEAN := TRUE;
    BEGIN
      o_success := NULL;
      IF p_session_id IS NOT NULL THEN
          BW_AMC.AMC_AUDIT.InitTransaction(v_transaction_id);
      END IF;

      v_setting_obj := JSON_OBJECT_T(p_user_settings);
      IF v_setting_obj.IS_OBJECT THEN
          v_user_id := v_setting_obj.get_string('USER_ID');
          v_range_id := v_setting_obj.get_string('RANGE_ID');
          v_amount_from := v_setting_obj.get_string('AMOUNT_FROM');
          v_amount_to := v_setting_obj.get_string('AMOUNT_TO');

          IF v_range_id IS NULL AND v_amount_from IS NULL AND v_amount_to IS NULL THEN
            o_success := 'no-amount-info';
            v_is_valid := FALSE;
          END IF;

          IF v_amount_from IS NOT NULL AND v_amount_to IS NOT NULL  AND v_is_valid = TRUE THEN
            IF IsNumeric(v_amount_from) = false OR IsNumeric(v_amount_to) = false THEN
              o_success := 'invalid-manual-range';
              v_is_valid := FALSE;
            END IF;

            IF TO_NUMBER(v_amount_from) >= TO_NUMBER(v_amount_to)  AND v_is_valid = TRUE THEN
              o_success := 'invalid-manual-range';
              v_is_valid := FALSE;
            END IF;

            IF v_range_id IS NOT NULL THEN
              o_success := 'both-amount-methods-provided';
              v_is_valid := FALSE;
            END IF;
          END IF;

          IF v_range_id IS NOT NULL AND v_is_valid = TRUE THEN
            IF IsNumeric(v_range_id) = false THEN
              o_success := 'invalid-predefined-range';
              v_is_valid := FALSE;
            END IF;
          END IF;

          IF  v_is_valid = TRUE THEN
            -- First insert/update user team assignment and amount ranges
            SELECT COUNT(*)
            INTO v_count
            FROM SYS_SUNDRY_TEAM_ASSIGN
            WHERE INSTITUTION_NUMBER = p_institution_number
              AND USER_ID = v_user_id
              AND EFFECTIVE_DATE = p_posting_date;

            v_params('INSTITUTION_NUMBER') := p_institution_number;
            v_params('USER_ID') := v_user_id;
            v_params('EFFECTIVE_DATE') := p_posting_date;
            v_params('TEAM_ID') := v_setting_obj.get_string('TEAM_ID');
            v_params('EXPIRY_DATE') := '99991231';
            v_params('RECORD_DATE') := p_posting_date;
            v_params('AUDIT_TRAIL') := BW_CODE_LIBRARY.CreateAuditTrail(v_user_id,'129');
            v_params('RANGE_CURRENCY') := v_setting_obj.get_string('CURRENCY');
            v_params('RANGE_START') := v_amount_from;
            v_params('RANGE_END') := v_amount_to;
            v_params('RANGE_ID') := v_range_id;

            -- If a record doesn't exist then insert it.
            IF v_count = 0 THEN
                v_sql := 'INSERT INTO SYS_SUNDRY_TEAM_ASSIGN (INSTITUTION_NUMBER, USER_ID, EFFECTIVE_DATE,
                                                            TEAM_ID, EXPIRY_DATE, RECORD_DATE, AUDIT_TRAIL,
                                                            RANGE_CURRENCY, RANGE_START, RANGE_END, RANGE_ID)
                            VALUES (:INSTITUTION_NUMBER, :USER_ID, :EFFECTIVE_DATE, :TEAM_ID, :EXPIRY_DATE,
                                    :RECORD_DATE, :AUDIT_TRAIL, :RANGE_CURRENCY, :RANGE_START, :RANGE_END, :RANGE_ID)';
            ELSE --update the existing record
                v_sql := 'UPDATE SYS_SUNDRY_TEAM_ASSIGN
                            SET TEAM_ID = :TEAM_ID,
                                RECORD_DATE = :RECORD_DATE,
                                AUDIT_TRAIL = :AUDIT_TRAIL,
                                RANGE_CURRENCY = :RANGE_CURRENCY,
                                RANGE_START = :RANGE_START,
                                RANGE_END = :RANGE_END,
                                RANGE_ID = :RANGE_ID
                            WHERE INSTITUTION_NUMBER = :INSTITUTION_NUMBER
                            AND USER_ID = :USER_ID
                            AND EFFECTIVE_DATE = :EFFECTIVE_DATE';
            END IF;

            BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);

            -- Now insert card organization skills.
            -- first query to see if the user already has any records
            SELECT COUNT(*)
            INTO v_count
            FROM SYS_SUNDRY_USER_PROCESS
            WHERE INSTITUTION_NUMBER = p_institution_number
            AND USER_ID = v_user_id;

            -- If records exist then delete them.
            IF v_count > 0 THEN
              v_params('INSTITUTION_NUMBER') := p_institution_number;
              v_params('USER_ID') := v_user_id;

              v_sql := 'DELETE FROM SYS_SUNDRY_USER_PROCESS
                        WHERE INSTITUTION_NUMBER = :INSTITUTION_NUMBER
                        AND USER_ID = :USER_ID';

              BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);
            END IF;

            -- Now loop through all card organization received and insert fresh records for each one.
            card_org_array := v_setting_obj.get_array('CARD_ORGANIZATIONS');
            FOR i IN 0..card_org_array.get_size - 1 LOOP
              card_org_obj := TREAT(card_org_array.get(i) AS JSON_OBJECT_T);
              IF card_org_obj.IS_OBJECT THEN
                v_card_org := card_org_obj.get_string('CARD_ORGANIZATION');

                v_params('INSTITUTION_NUMBER') := p_institution_number;
                v_params('CARD_ORGANIZATION') := v_card_org;
                v_params('USER_ID') := v_user_id;
                v_params('RULE_TYPE') := '999';
                v_params('TRANSACTION_CATEGORY') := '999';
                v_params('RECORD_DATE') := p_posting_date;
                v_params('AUDIT_TRAIL') := BW_CODE_LIBRARY.CreateAuditTrail(v_user_id,'129');
                v_params('PAYMENT_PROCESSOR_ID') := '999';

                v_sql := 'INSERT INTO SYS_SUNDRY_USER_PROCESS (INSTITUTION_NUMBER, CARD_ORGANIZATION, USER_ID,
                                                            RULE_TYPE, TRANSACTION_CATEGORY, RECORD_DATE, AUDIT_TRAIL,
                                                            PAYMENT_PROCESSOR_ID)
                        VALUES (:INSTITUTION_NUMBER, :CARD_ORGANIZATION, :USER_ID, :RULE_TYPE, :TRANSACTION_CATEGORY,
                                :RECORD_DATE, :AUDIT_TRAIL, :PAYMENT_PROCESSOR_ID)';

                BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);
              END IF;
            END LOOP;

            -- Finally insert queue mappings
            -- first query to see if user already has any records
            SELECT COUNT(*)
            INTO v_count
            FROM SYS_DISPUTE_QUEUE_MAPPING
            WHERE INSTITUTION_NUMBER = p_institution_number
            AND USER_ID = v_user_id;

            -- If records exist then delete them.
            IF v_count > 0 THEN
              v_params('INSTITUTION_NUMBER') := p_institution_number;
              v_params('USER_ID') := v_user_id;

              v_sql := 'DELETE FROM SYS_DISPUTE_QUEUE_MAPPING
                        WHERE INSTITUTION_NUMBER = :INSTITUTION_NUMBER
                        AND USER_ID = :USER_ID';

              BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);
            END IF;

            -- Now loop through all queues received and insert fresh records for each one.
            queue_array := v_setting_obj.get_array('QUEUES');
            FOR i IN 0..queue_array.get_size - 1 LOOP
              queue_obj := TREAT(queue_array.get(i) AS JSON_OBJECT_T);
              IF queue_obj.IS_OBJECT THEN
                v_queue_id := queue_obj.get_string('QUEUE');
                v_queue_priority := queue_obj.get_string('PRIORITY');

                v_params('INSTITUTION_NUMBER') := p_institution_number;
                v_params('DISPUTE_QUEUE_ID') := v_queue_id;
                v_params('USER_ID') := v_user_id;
                v_params('PRIORITY') := v_queue_priority;
                v_params('AUDIT_TRAIL') := BW_CODE_LIBRARY.CreateAuditTrail(v_user_id,'129');

                v_sql := 'INSERT INTO SYS_DISPUTE_QUEUE_MAPPING (INSTITUTION_NUMBER, DISPUTE_QUEUE_ID,
                                                                USER_ID, PRIORITY, AUDIT_TRAIL)
                          VALUES (:INSTITUTION_NUMBER, :DISPUTE_QUEUE_ID, :USER_ID, :PRIORITY,
                                  :AUDIT_TRAIL)';

                BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);
              END IF;
            END LOOP;
          END IF;
      END IF;
    EXCEPTION
      WHEN OTHERS THEN
        o_success := '000';
        raise_application_error(-20001,'An error was encountered while saving user settings: '||SQLERRM);
  END;

  PROCEDURE delete_user_settings(
    p_institution_number  IN  SYS_DISPUTE_QUEUES.INSTITUTION_NUMBER%TYPE,
    p_user_id             IN  SYS_USER_INFORMATION.USERID%TYPE,
    p_source              IN  GUI_MASK_MENU.MASK_NAME%TYPE,
    p_session_id          IN  NUMBER DEFAULT NULL,
    o_success             OUT BWT_CONFIRMATION.INDEX_FIELD%TYPE
  ) AS
    v_params            BW_DISPUTES_CORE.ASSOC_ARRAY;
    v_sql               VARCHAR2(4000);
    v_transaction_id    NUMBER;
    v_count             NUMBER;
  BEGIN
      o_success := '001';
      IF p_session_id IS NOT NULL THEN
          BW_AMC.AMC_AUDIT.InitTransaction(v_transaction_id);
      END IF;

      v_params('INSTITUTION_NUMBER') := p_institution_number;
      v_params('USER_ID') := p_user_id;

      -- First delete user team assignment
      v_sql := 'DELETE FROM SYS_SUNDRY_TEAM_ASSIGN
                WHERE INSTITUTION_NUMBER = :INSTITUTION_NUMBER
                AND USER_ID = :USER_ID';

      BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);

      -- Delete card organization skills.
      v_sql := 'DELETE FROM SYS_SUNDRY_USER_PROCESS
                WHERE INSTITUTION_NUMBER = :INSTITUTION_NUMBER
                AND USER_ID = :USER_ID';

      BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);

      -- Finally delete queue mappings
      v_sql := 'DELETE FROM SYS_DISPUTE_QUEUE_MAPPING
                WHERE INSTITUTION_NUMBER = :INSTITUTION_NUMBER
                AND USER_ID = :USER_ID';

      BW_DISPUTES_CORE.execute_statement(v_sql, v_params, p_source, p_session_id, v_transaction_id);

    EXCEPTION
      WHEN OTHERS THEN
        raise_application_error(-20001,'An error was encountered while deleting user settings: '||SQLERRM);
        o_success := '000';
  END;

  /**
  * Procedure that refreshes the queue cache if the data in the SYS_DISPUTE_QUEUE_CACHE is expired or
  * populates it if there is no data.
  *
  * @param p_institution_number   The institution number.
  * @param p_posting_date         The posting date of the current institution.
  * @param p_default_language     The default language for the current institution.
  *
  */
  PROCEDURE refresh_queue_cache(
    p_institution_number  IN  SYS_DISPUTE_QUEUES.INSTITUTION_NUMBER%TYPE,
    p_posting_date        IN  SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format),
    p_default_language    IN  BWT_DISPUTE_QUEUE_ID.LANGUAGE%TYPE DEFAULT 'USA'
  ) AS
    v_datetime_format       VARCHAR2(19) := c_date_format ||' '|| c_time_format;
    v_queue_cache_expiry    VARCHAR2(3);
    v_current_datetime      VARCHAR2(17);
    v_inner_queue_query     CLOB;
    v_expiry_timestamp      TIMESTAMP;
    v_expired_cache_count   NUMBER;
    v_sql                   CLOB;
  BEGIN
    --Retrieve the queue cache expiry from config.
    BEGIN
    SELECT CONFIG_VALUE
    INTO v_queue_cache_expiry
    FROM SYS_CONFIGURATION
    WHERE INSTITUTION_NUMBER = p_institution_number
    AND CONFIG_SECTION = 'Web-GUI'
    AND CONFIG_KEYWORD = 'QueueCacheExpiry';
    EXCEPTION WHEN
      NO_DATA_FOUND THEN
      v_queue_cache_expiry := c_default_queue_cache_expiry;
    END;

    --Calculate the expiry timestamp
    v_expiry_timestamp := CURRENT_TIMESTAMP - NUMTODSINTERVAL(v_queue_cache_expiry, 'MINUTE');

    --Check if the records are expired
    SELECT COUNT(*)
    INTO v_expired_cache_count
    FROM SYS_DISPUTE_QUEUE_CACHE
    WHERE RECORD_DATE <= v_expiry_timestamp
    AND INSTITUTION_NUMBER = p_institution_number;

    IF v_expired_cache_count > 0 THEN
      DELETE FROM SYS_DISPUTE_QUEUE_CACHE
      WHERE INSTITUTION_NUMBER = p_institution_number;
    END IF;

    --Check if any records are left
    SELECT COUNT(*)
    INTO v_expired_cache_count
    FROM SYS_DISPUTE_QUEUE_CACHE
    WHERE INSTITUTION_NUMBER = p_institution_number;

    --If no records are found then populate the table.
    IF v_expired_cache_count = 0 THEN
      v_current_datetime := TO_CHAR(CURRENT_TIMESTAMP, v_datetime_format);

      DECLARE
      CURSOR cur_dispute_queue IS
        SELECT INDEX_FIELD
        FROM BWT_DISPUTE_QUEUE_ID
        WHERE INSTITUTION_NUMBER = p_institution_number
        AND LANGUAGE = p_default_language;
      BEGIN
        FOR cursor_record IN cur_dispute_queue LOOP
          BEGIN
            v_inner_queue_query := bw_dispute_automation.get_queue_query(p_institution_number,
                                                                         p_posting_date,
                                                                         cursor_record.INDEX_FIELD);

            --Output an error when an exception is found when trying to retrieve SQL from GET_QUEUE_QUERY.
            EXCEPTION WHEN OTHERS THEN
                RAISE_APPLICATION_ERROR(-20001, 'Error occurred when trying to retrieve data for Queue ' || cursor_record.INDEX_FIELD || ' .');
          END;
          BEGIN
            v_sql := 'INSERT INTO SYS_DISPUTE_QUEUE_CACHE (QUEUE_ID, INSTITUTION_NUMBER, RECORD_DATE, CASE_NUMBER) (
                      SELECT /*+materialize leading(hd lst) use_concat*/'''
                                 || cursor_record.INDEX_FIELD || ''' ,'''
                                 || p_institution_number  || ''', '
                                 || 'TO_DATE( ''' || v_current_datetime|| ''','''|| v_datetime_format|| '''), case_number FROM '
                                 || v_inner_queue_query || ')';

            EXECUTE IMMEDIATE v_sql;

            --Output an error when an exception is found when trying to insert the data into SYS_DISPUTE_QUEUE_CACHE.
            EXCEPTION WHEN OTHERS THEN
                RAISE_APPLICATION_ERROR(-20001, 'Error occurred when trying to insert data for Queue ' || cursor_record.INDEX_FIELD || ' .');
          END;
        END LOOP;
      END;
    END IF;
  END;

  FUNCTION get_user_queues(
    p_institution_number  SYS_DISPUTE_QUEUE_MAPPING.INSTITUTION_NUMBER%TYPE,
    p_user_id             SYS_DISPUTE_QUEUE_MAPPING.USER_ID%TYPE,
    p_current_language    BWT_DISPUTE_QUEUE_ID.LANGUAGE%TYPE DEFAULT 'USA',
    p_default_language    BWT_DISPUTE_QUEUE_ID.LANGUAGE%TYPE DEFAULT 'USA'
  )
  RETURN TBL_USER_DISPUTE_QUEUES PIPELINED AS

    CURSOR cur_user_queues IS
      SELECT QID.INDEX_FIELD, QID.DISPUTE_QUEUE, QMAP.PRIORITY
      FROM SYS_DISPUTE_QUEUE_MAPPING QMAP, BWT_DISPUTE_QUEUE_ID QID
      WHERE QMAP.INSTITUTION_NUMBER = p_institution_number
      AND QMAP.USER_ID = p_user_id
      AND QID.INSTITUTION_NUMBER = QMAP.INSTITUTION_NUMBER
      AND QID.INDEX_FIELD = QMAP.DISPUTE_QUEUE_ID
      AND QID.LANGUAGE = CASE WHEN EXISTS(SELECT INDEX_FIELD
                                           FROM BWT_DISPUTE_QUEUE_ID
                                           WHERE LANGUAGE = p_current_language
                                           AND INSTITUTION_NUMBER = QID.INSTITUTION_NUMBER
                                           AND INDEX_FIELD = QID.INDEX_FIELD)
                         THEN p_current_language
                         ELSE p_default_language
                         END
      ORDER BY QMAP.PRIORITY;

  BEGIN
    FOR cursor_record IN cur_user_queues LOOP
      PIPE ROW (cursor_record);
    END LOOP;
  END get_user_queues;

  FUNCTION build_queue_query(
    p_queue_order_by  SYS_DISPUTE_QUEUES.ORDER_BY%TYPE,
    p_queue_query     CLOB
  ) RETURN CLOB
  AS
    v_queue_query CLOB;
  BEGIN
    -- Always appending CASE_NUMBER  to show consistent order when having equal priority cases.
    v_queue_query := 'SELECT  ROW_NUMBER() OVER (ORDER BY '|| p_queue_order_by ||', CASE_NUMBER) AS POSITION,
                              QUEUE_CASES.*
                      FROM (
                        WITH CASES AS (
                        SELECT  /*+materialize leading(hd lst) use_concat*/
                                    INSTITUTION_NUMBER,
                                    ACQUIRER_REFERENCE,
                                    CASE_NUMBER,
                                    CASE_STATUS,
                                    CARD_ORGANIZATION,
                                    NVL(BW_DISPUTES.get_chargeback_reason(INSTITUTION_NUMBER,
                                                                            ACQUIRER_REFERENCE),
                                            :c_generic_chargeback_reason) AS CHARGEBACK_REASON,
                                    BW_DISPUTES.get_dispute_condition(INSTITUTION_NUMBER,
                                                                        ACQUIRER_REFERENCE) AS DISPUTE_CONDITION,
                                    BW_DISPUTES.get_retrieval_reason(INSTITUTION_NUMBER,
                                                                        ACQUIRER_REFERENCE)  AS RETRIEVAL_REASON,
                                    ESCALATED_FLAG,
                                    OWNER_ID
                            FROM
                            '|| p_queue_query ||'
                        )

                        SELECT
                                CASES.INSTITUTION_NUMBER,
                                CASES.ACQUIRER_REFERENCE,
                                CASES.CASE_NUMBER,
                                CASES.OWNER_ID,
                                TYPE.SUNDRY_TYPE AS CASE_TYPE,
                                STATUS.CASE_STATUS,
                                ACTION.ACTION_TAKEN AS LAST_ACTION,
                                TO_NUMBER(SND_TRN.TRAN_AMOUNT_GR) AS AMOUNT,
                                SND_TRN.TRAN_CURRENCY AS CURRENCY,
                                TO_NUMBER(SND_TRN.SETTLEMENT_AMOUNT_GR) AS SETTLEMENT_AMOUNT,
                                SND_TRN.SETTLEMENT_CURRENCY,
                                CRD_ORG.CARD_ORGANIZATION,
                                NVL(
                                  CASES.RETRIEVAL_REASON,
                                  CASE CASES.CARD_ORGANIZATION
                                  WHEN :c_visa_card_org THEN
                                      /*Visa cases use VCR reason codes, hence dispute_condition.
                                      But Visa pin debit disputes still use old reason codes.*/
                                      NVL(CASES.DISPUTE_CONDITION, CASES.CHARGEBACK_REASON)
                                  ELSE
                                      CASES.CHARGEBACK_REASON
                                  END) AS REASON,
                                BW_DISPUTES.get_reason_description( CASES.INSTITUTION_NUMBER,
                                                                  CASES.CHARGEBACK_REASON,
                                                                  CASES.DISPUTE_CONDITION,
                                                                  CASES.RETRIEVAL_REASON,
                                                                  :p_current_language,
                                                                  CASES.CARD_ORGANIZATION,
                                                                  :p_default_language
                                                                  ) AS REASON_DESCRIPTION,
                                TO_NUMBER(BW_DISPUTES.get_days_to_action(CASES.INSTITUTION_NUMBER,
                                                              CASES.ACQUIRER_REFERENCE,
                                                              :p_date,
                                                              LAST_CENTRAL_DATE.CENTRAL_DATE)) AS DAYS_TO_ACTION,
                                TO_NUMBER(BW_DISPUTES.get_days_to_card_scheme_deadline(CASES.CASE_NUMBER,
                                                                            CASES.INSTITUTION_NUMBER,
                                                                            :p_date
                                                                            )) AS DAYS_TO_CARD_SCHEME_DEADLINE,
                                BW_LIB_DATE.DATEGETDIFF(:p_date, LAST_REC.RECORD_DATE, 0) AS AGE_OF_CASE,
                                NVL(CASES.ESCALATED_FLAG, :c_confirmation_no) AS ESCALATED_FLAG,
                                LAST_CENTRAL_DATE.CENTRAL_DATE AS CENTRAL_DATE
                        FROM CASES,
                            INT_SUNDRY_HISTORY LAST_REC,
                            INT_SUNDRY_HISTORY LAST_SUNDRY_SLIP,
                            INT_SUNDRY_HISTORY LAST_CENTRAL_DATE,
                            INT_SUNDRY_TRANSACTIONS SND_TRN,
                            BWT_SUNDRY_TYPE TYPE,
                            BWT_CASE_STATUS STATUS,
                            BWT_ACTION_TAKEN ACTION,
                            BWT_CARD_ORGANIZATION CRD_ORG
                        WHERE
                                LAST_REC.INSTITUTION_NUMBER = CASES.INSTITUTION_NUMBER
                            AND LAST_REC.SUNDRY_HISTORY_ID = BW_DISPUTES.get_max_history_id(CASES.INSTITUTION_NUMBER,
                                                                                            CASES.ACQUIRER_REFERENCE)

                            AND LAST_SUNDRY_SLIP.INSTITUTION_NUMBER = CASES.INSTITUTION_NUMBER
                            AND LAST_SUNDRY_SLIP.SUNDRY_HISTORY_ID = (SELECT MAX(SUNDRY_HISTORY_ID)
                                                                    FROM INT_SUNDRY_HISTORY
                                                                    WHERE INSTITUTION_NUMBER = CASES.INSTITUTION_NUMBER
                                                                        AND ACQUIRER_REFERENCE = CASES.ACQUIRER_REFERENCE
                                                                        AND SUNDRY_TRANSACTION_SLIP IS NOT NULL
                                                                        AND RULE_ACTION NOT IN (:c_chargeback_fee,
                                                                                                :c_retrieval_fee))

                            AND SND_TRN.INSTITUTION_NUMBER = CASES.INSTITUTION_NUMBER
                            AND SND_TRN.SUNDRY_TYPE = LAST_SUNDRY_SLIP.SUNDRY_TYPE
                            AND SND_TRN.SUNDRY_TRANSACTION_SLIP = LAST_SUNDRY_SLIP.SUNDRY_TRANSACTION_SLIP

                            AND LAST_CENTRAL_DATE.INSTITUTION_NUMBER = CASES.INSTITUTION_NUMBER
                            AND LAST_CENTRAL_DATE.SUNDRY_HISTORY_ID = (SELECT MAX(SUNDRY_HISTORY_ID)
                                                                    FROM INT_SUNDRY_HISTORY
                                                                    WHERE INSTITUTION_NUMBER = CASES.INSTITUTION_NUMBER
                                                                        AND ACQUIRER_REFERENCE = CASES.ACQUIRER_REFERENCE
                                                                        AND CENTRAL_DATE IS NOT NULL)
                            AND TYPE.INSTITUTION_NUMBER = LAST_REC.INSTITUTION_NUMBER
                            AND TYPE.INDEX_FIELD = LAST_REC.SUNDRY_TYPE
                            AND TYPE.LANGUAGE = CASE
                                                    WHEN EXISTS(
                                                        SELECT INDEX_FIELD
                                                        FROM BWT_SUNDRY_TYPE
                                                        WHERE LANGUAGE = :p_current_language
                                                          AND INSTITUTION_NUMBER = TYPE.INSTITUTION_NUMBER
                                                          AND INDEX_FIELD = TYPE.INDEX_FIELD)
                                                      THEN :p_current_language
                                                    ELSE :p_default_language
                                                    END

                            AND STATUS.INSTITUTION_NUMBER = CASES.INSTITUTION_NUMBER
                            AND STATUS.INDEX_FIELD = CASES.CASE_STATUS
                            AND STATUS.LANGUAGE = CASE
                                                      WHEN EXISTS(
                                                          SELECT INDEX_FIELD
                                                          FROM BWT_CASE_STATUS
                                                          WHERE LANGUAGE = :p_current_language
                                                            AND INSTITUTION_NUMBER = STATUS.INSTITUTION_NUMBER
                                                            AND INDEX_FIELD = STATUS.INDEX_FIELD)
                                                        THEN :p_current_language
                                                      ELSE :p_default_language
                                                      END

                            AND ACTION.INSTITUTION_NUMBER = LAST_REC.INSTITUTION_NUMBER
                            AND ACTION.INDEX_FIELD = LAST_REC.RULE_ACTION
                            AND ACTION.LANGUAGE = CASE
                                              WHEN EXISTS(
                                                  SELECT INDEX_FIELD
                                                  FROM BWT_ACTION_TAKEN
                                                  WHERE LANGUAGE = :p_current_language
                                                    AND INSTITUTION_NUMBER = ACTION.INSTITUTION_NUMBER
                                                    AND INDEX_FIELD = ACTION.INDEX_FIELD)
                                                THEN :p_current_language
                                              ELSE :p_default_language
                                              END

                            AND CRD_ORG.INSTITUTION_NUMBER = CASES.INSTITUTION_NUMBER
                            AND CRD_ORG.INDEX_FIELD = CASES.CARD_ORGANIZATION
                            AND CRD_ORG.LANGUAGE = CASE
                                              WHEN EXISTS(
                                                  SELECT INDEX_FIELD
                                                  FROM BWT_CARD_ORGANIZATION
                                                  WHERE LANGUAGE = :p_current_language
                                                    AND INSTITUTION_NUMBER = CRD_ORG.INSTITUTION_NUMBER
                                                    AND INDEX_FIELD = CRD_ORG.INDEX_FIELD)
                                                THEN :p_current_language
                                              ELSE :p_default_language
                                              END
                      ) QUEUE_CASES ';
    RETURN v_queue_query;
  END;

  FUNCTION fetch_queue_disputes(
    p_institution_number  SYS_DISPUTE_QUEUES.INSTITUTION_NUMBER%TYPE,
    p_queue_id            SYS_DISPUTE_QUEUES.DISPUTE_QUEUE_ID%TYPE,
    p_current_language    VARCHAR2 DEFAULT 'USA',
    p_default_language    VARCHAR2 DEFAULT 'USA',
    p_date                VARCHAR2 DEFAULT TO_CHAR(SYSDATE, 'YYYYMMDD')
  ) RETURN TBL_QUEUE_DISPUTES PIPELINED
  AS
    v_ref_cur           SYS_REFCURSOR;
    v_cursor_rec        ROW_QUEUE_DISPUTES;
    v_sql               CLOB;
    v_queue_inner_query CLOB;
    v_queue_query       CLOB;
    v_queue_order_by    SYS_DISPUTE_QUEUES.ORDER_BY%TYPE;
  BEGIN
    -- Get the queue's inner query, but remove the select caluse as we will replace it.
    v_queue_inner_query := bw_dispute_automation.get_queue_query(p_institution_number, p_date, p_queue_id);

    -- Get the queue's order by. If null defaults to the CASE_NUMBER column.
    SELECT NVL(ORDER_BY, 'DAYS_TO_CARD_SCHEME_DEADLINE')
    INTO v_queue_order_by
    FROM SYS_DISPUTE_QUEUES
    WHERE INSTITUTION_NUMBER = p_institution_number
    AND DISPUTE_QUEUE_ID = p_queue_id;

    -- Get the query which retrieves the required columns for queue view.
    v_queue_query := build_queue_query(v_queue_order_by, v_queue_inner_query);

    v_sql := 'SELECT * FROM (' || v_queue_query || ') ORDER BY POSITION';

    OPEN v_ref_cur FOR v_sql
    USING c_generic_chargeback_reason,
          c_visa_card_org,
          p_current_language,
          p_default_language,
          p_date,
          p_date,
          p_date,
          c_confirmation_no,
          c_chargeback_fee,
          c_retrieval_fee,
          p_current_language,
          p_current_language,
          p_default_language,
          p_current_language,
          p_current_language,
          p_default_language,
          p_current_language,
          p_current_language,
          p_default_language,
          p_current_language,
          p_current_language,
          p_default_language;
    LOOP
      FETCH v_ref_cur INTO v_cursor_rec;
      EXIT WHEN v_ref_cur%NOTFOUND;
      PIPE ROW (v_cursor_rec);
    END LOOP;
  END fetch_queue_disputes;

  FUNCTION get_next_case(p_institution_number   INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
                        p_user_id               SYS_DISPUTE_QUEUE_MAPPING.USER_ID%TYPE,
                        p_current_language      BWT_CHARGEBACK_REASON.LANGUAGE%TYPE DEFAULT 'USA',
                        p_default_language      BWT_CHARGEBACK_REASON.LANGUAGE%TYPE DEFAULT 'USA',
                        p_date                  VARCHAR2 DEFAULT TO_CHAR(SYSDATE, 'YYYYMMDD')
  ) RETURN INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE AS
    v_sql               CLOB;
    v_queue_inner_query CLOB;
    v_queue_query       CLOB;
    v_queue_order_by    SYS_DISPUTE_QUEUES.ORDER_BY%TYPE;
    v_record_count      NUMBER;
    v_case_number       INT_SUNDRY_HISTORY.CASE_NUMBER%TYPE := NULL;

    -- Get all the user's queues in order of priority
    CURSOR queue_cursor IS
      SELECT DISPUTE_QUEUE_ID
      FROM SYS_DISPUTE_QUEUE_MAPPING
      WHERE INSTITUTION_NUMBER = p_institution_number
      AND USER_ID = p_user_id
      ORDER BY TO_NUMBER(PRIORITY);

  BEGIN
    -- Loop through each queue until one with data is found
    FOR cursor_record IN queue_cursor LOOP
      -- Get the queue's inner query, but remove the select clause as we will replace it.
      v_queue_inner_query := bw_dispute_automation.get_queue_query(p_institution_number, p_date, cursor_record.DISPUTE_QUEUE_ID);

      -- Ensure user has skills for the cases
      v_sql := 'SELECT COUNT(*) FROM '
                || v_queue_inner_query
                || ' AND BW_DISPUTE_AUTOMATION.has_required_skills(:p_user_id, :p_institution_number, CASE_NUMBER, :p_date) = :c_confirmation_yes'
                || ' AND owner_id IS NULL'
                ;

      EXECUTE IMMEDIATE v_sql
      INTO v_record_count
      USING p_user_id, p_institution_number, p_date, c_confirmation_yes;

      --This queue has cases which the user has the skills for, so return the first case according to the queue order
      IF v_record_count > 0 THEN
        -- Get the queue's order by. If null defaults to the CASE_NUMBER column.
        SELECT NVL(ORDER_BY, 'CASE_NUMBER')
        INTO v_queue_order_by
        FROM SYS_DISPUTE_QUEUES
        WHERE INSTITUTION_NUMBER = p_institution_number
        AND DISPUTE_QUEUE_ID = cursor_record.DISPUTE_QUEUE_ID;

        -- Get the query which retrieves the required columns for queue view.
        v_queue_query := build_queue_query(v_queue_order_by, v_queue_inner_query);

        -- Query for the highest priority case number which the user has skills for.
        v_sql := 'SELECT CASE_NUMBER FROM ('
                  || v_queue_query
                  || ' WHERE BW_DISPUTE_AUTOMATION.has_required_skills(:p_user_id, :p_institution_number, CASE_NUMBER, :p_date) = :c_confirmation_yes
                  AND owner_id IS NULL
                  ) WHERE POSITION = 1';

        EXECUTE IMMEDIATE v_sql
        INTO v_case_number
        USING c_generic_chargeback_reason,
              c_visa_card_org,
              p_current_language,
              p_default_language,
              p_date,
              p_date,
              p_date,
              c_confirmation_no,
              c_chargeback_fee,
              c_retrieval_fee,
              p_current_language,
              p_current_language,
              p_default_language,
              p_current_language,
              p_current_language,
              p_default_language,
              p_current_language,
              p_current_language,
              p_default_language,
              p_current_language,
              p_current_language,
              p_default_language,
              p_user_id,
              p_institution_number,
              p_date,
              c_confirmation_yes;

        -- exit the loop as we have got our case number and there is no point continuing.
        EXIT;
      END IF;
    END LOOP;
    RETURN v_case_number;
  END get_next_case;

  /**
  * Returns PIPELINED output consisting of QUEUE_NAME and the CASE_COUNT retrieved from the SYS_DISPUTE_QUEUE_CACHE table.
  *
  * @param p_institution_number   The institution number which the case is for.
  * @param p_posting_date         The posting date for the current institution.
  * @param p_current_language     The current language of the current institution.
  * @param p_default_language     The default language of the current institution.
  *
  * @return TBL_QUEUE_CASE_COUNT  PIPELINED output consisting of QUEUE_NAME and the CASE_COUNT.
  */
  FUNCTION get_queue_case_count(p_institution_number   INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
                                p_posting_date         SYS_POSTING_DATE.POSTING_DATE%TYPE DEFAULT TO_CHAR(SYSDATE, c_date_format),
                                p_current_language     VARCHAR2 DEFAULT 'USA',
                                p_default_language     VARCHAR2 DEFAULT 'USA')
  RETURN TBL_QUEUE_CASE_COUNT PIPELINED AS
    CURSOR tbl_cursor IS
      SELECT DISP.DISPUTE_QUEUE AS QUEUE_NAME,
             COUNT(CACHE.CASE_NUMBER) AS CASE_COUNT
        FROM SYS_DISPUTE_QUEUE_CACHE CACHE,
        BWT_DISPUTE_QUEUE_ID DISP
        WHERE DISP.INSTITUTION_NUMBER = p_institution_number
        AND DISP.INSTITUTION_NUMBER = CACHE.INSTITUTION_NUMBER (+)
        AND DISP.INDEX_FIELD = CACHE.QUEUE_ID (+)
        AND DISP.LANGUAGE = CASE WHEN EXISTS(SELECT INDEX_FIELD
                                             FROM BWT_DISPUTE_QUEUE_ID
                                             WHERE LANGUAGE = p_current_language
                                             AND INSTITUTION_NUMBER = DISP.INSTITUTION_NUMBER
                                             AND INDEX_FIELD = DISP.INDEX_FIELD)
                                 THEN p_current_language
                                 ELSE p_default_language
                                 END
        GROUP BY DISP.DISPUTE_QUEUE
        ORDER BY DISP.DISPUTE_QUEUE;
    BEGIN
        FOR cursor_record IN tbl_cursor LOOP
          PIPE ROW (cursor_record);
    END LOOP;
  END get_queue_case_count;

  /**
  * Returns a pipeline with the presentment record details
  *
  * @param p_institution_number   The institution number which the presentment is for
  * @param p_case_number          The case number to get the presentment for
  *
  * @return TBL_PRESENTMENT_DETAILS       A table object containing the presentment details
  */
  FUNCTION fetch_presentment_details(p_institution_number INT_TRANSACTIONS.INSTITUTION_NUMBER%TYPE,
                                     p_case_number        INT_TRANSACTIONS.TRANSACTION_SLIP%TYPE)
    RETURN TBL_PRESENTMENT_DETAILS
    PIPELINED AS
      CURSOR tbl_cursor IS
                SELECT TRN.RETRIEVAL_REFERENCE,
                       TRN.CARD_NUMBER,
                       TRN.AUTH_CODE,
                       TRN.TRAN_CURRENCY as PRES_CURRENCY,
                       TRN.TRAN_AMOUNT_GR as PRES_AMOUNT,
                       TRN.VALUE_DATE
                FROM INT_TRANSACTIONS TRN
                WHERE TRN.INSTITUTION_NUMBER = p_institution_number
                AND TRN.TRANSACTION_SLIP = p_case_number;
    BEGIN
      FOR cursor_record IN tbl_cursor LOOP
        PIPE ROW (cursor_record);
      END LOOP;
    END fetch_presentment_details;

  /**
  * Returns a varchar2() with the message from scheme
  *
  * @param p_institution_number   The institution number which the message is from.
  * @param p_acquirer_reference   The acquirer reference to get the message from.
  * @param p_current_language   The current language of the current institution.
  * @param p_default_language   The default language of the current institution.
  *
  * @return v_message_from_scheme       A varchar2 object containing the message from scheme
  */
  FUNCTION get_reject_message(p_institution_number   INT_SUNDRY_HISTORY.INSTITUTION_NUMBER%TYPE,
                              p_acquirer_reference   INT_SUNDRY_HISTORY.ACQUIRER_REFERENCE%TYPE,
                              p_current_language     BWT_CHARGEBACK_REASON.LANGUAGE%TYPE DEFAULT 'USA',
                              p_default_language     BWT_CHARGEBACK_REASON.LANGUAGE%TYPE DEFAULT 'USA'
  ) RETURN VARCHAR2 AS
    v_message_from_scheme               VARCHAR2(50);
  BEGIN
    SELECT RJCT_MSG.REJECT_REASON || ' - ' ||  RJCT_MSG.REJECT_DESCRIPTION
    INTO v_message_from_scheme
      FROM  INT_SUNDRY_TRANSACTIONS SND,
            INT_SUNDRY_HISTORY HST,
            BWT_REPROCESS_REJECTS RJCT_MSG
      WHERE HST.INSTITUTION_NUMBER = p_institution_number
      AND HST.ACQUIRER_REFERENCE = p_acquirer_reference
      AND HST.SUNDRY_HISTORY_ID IN (SELECT MAX(SUNDRY_HISTORY_ID)
                                    FROM INT_SUNDRY_HISTORY
                                    WHERE INSTITUTION_NUMBER = HST.INSTITUTION_NUMBER
                                    AND ACQUIRER_REFERENCE = HST.ACQUIRER_REFERENCE
                                    AND RULE_ACTION = c_miscellaneous_action
                                    AND SUNDRY_TYPE = c_rejects
                                    AND CARD_ORGANIZATION = c_discover_card_org
                                    AND SUNDRY_TRANSACTION_SLIP IS NOT NULL)
      AND SND.INSTITUTION_NUMBER = HST.INSTITUTION_NUMBER
      AND SND.SUNDRY_TRANSACTION_SLIP = HST.SUNDRY_TRANSACTION_SLIP
      AND RJCT_MSG.INSTITUTION_NUMBER = HST.INSTITUTION_NUMBER
      AND RJCT_MSG.REJECT_REASON = DECODE(SND.MESSAGE_SUNDRY_TEXT, '',
                                        '',
                                        SUBSTR(SND.MESSAGE_SUNDRY_TEXT, 0, INSTR(SND.MESSAGE_SUNDRY_TEXT, ' ', 1) - 1))
      AND RJCT_MSG.LANGUAGE = CASE
                                WHEN EXISTS(
                                    SELECT INDEX_FIELD
                                    FROM BWT_REPROCESS_REJECTS
                                    WHERE LANGUAGE = p_current_language
                                          AND INSTITUTION_NUMBER = RJCT_MSG.INSTITUTION_NUMBER
                                          AND REJECT_REASON = RJCT_MSG.REJECT_REASON)
                                  THEN p_current_language
                                ELSE p_default_language
                              END;
    RETURN v_message_from_scheme;
  END get_reject_message;

END;
/
