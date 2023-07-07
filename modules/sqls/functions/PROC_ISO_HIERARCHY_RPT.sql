FUNCTION proc_iso_hierarchy_rpt(
    p_institution_number IN VARCHAR2,
    p_client_number IN VARCHAR2
)
  return tab_iso pipelined
as

  v_clientTypes varchar2(200);

begin

  BEGIN
    SELECT CONFIG_VALUE INTO v_clientTypes FROM SYS_CONFIGURATION WHERE INSTITUTION_NUMBER = p_institution_number AND CONFIG_KEYWORD = 'ISOHierarchyClientTypes';

    EXCEPTION WHEN no_data_found THEN
    BEGIN
      SELECT CONFIG_VALUE INTO v_clientTypes FROM SYS_CONFIGURATION WHERE INSTITUTION_NUMBER = '00000000' AND CONFIG_KEYWORD = 'ISOHierarchyClientTypes';
      EXCEPTION WHEN no_data_found THEN

      BEGIN
        v_clientTypes := bwtpad('BWT_CLIENT_TYPE', '010') || ',' || bwtpad('BWT_CLIENT_TYPE', '011') || ',' || bwtpad('BWT_CLIENT_TYPE', '012') || ',' ||   bwtpad('BWT_CLIENT_TYPE', '013') || ',' ||
                   bwtpad('BWT_CLIENT_TYPE', '002') || ',' ||  bwtpad('BWT_CLIENT_TYPE', '014') || ',' || bwtpad('BWT_CLIENT_TYPE', '009');
      END;
    END;

  END;


  --
  for rec in (

SELECT CLIENT_NUMBER,
       DEST_CLIENT_NUMBER,
       CLIENT_LEVEL,
       CLIENT_TYPE,
       INSTITUTION_NUMBER,
       INSTITUTION_NAME,
       TRADE_NAME,
       GROUP_NUMBER,
       CLIENT_TYPE_INDEX,
       CBP,
       CLIENT_STATUS
FROM
  (SELECT hrc.EFFECTIVE_DATE,
          hrc.INSTITUTION_NUMBER,
          hrc.INSTITUTION_NAME,
          hrc.CLIENT_NUMBER,
          hrc.CLIENT_TYPE,
          hrc.DEST_CLIENT_NUMBER,
          hrc.TRADE_NAME,
          hrc.GROUP_NUMBER,
          hrc.CLIENT_LEVEL,
          hrc.CLIENT_TYPE_INDEX,
          SYS_CONNECT_BY_PATH(hrc.CLIENT_NUMBER,'/') CBP,
          CLIENT_STATUS
   FROM
     (SELECT MAX(CRL.EFFECTIVE_DATE) AS EFFECTIVE_DATE,
             LIC.INSTITUTION_NUMBER,
             LIC.INSTITUTION_NAME,
             CRL.CLIENT_NUMBER,
             BCTYPE.CLIENT_TYPE,
             CRL.DEST_CLIENT_NUMBER,
             DET.TRADE_NAME,
             CRL.GROUP_NUMBER,
             NULL AS CLIENT_LEVEL,
             DET.CLIENT_TYPE AS CLIENT_TYPE_INDEX,
             DET.CLIENT_STATUS
      FROM SYS_INSTITUTION_LICENCE LIC,
           CIS_CLIENT_DETAILS DET,
           CIS_CLIENT_RELATION_LINKS CRL,
           BWT_CLIENT_TYPE BCTYPE
      WHERE LIC.INSTITUTION_NUMBER = CRL.INSTITUTION_NUMBER
        AND lic.institution_number = p_institution_number
        AND DET.INSTITUTION_NUMBER = CRL.INSTITUTION_NUMBER
        AND CRL.INSTITUTION_NUMBER = CRL.DEST_INSTITUTION_NUMBER
        AND CRL.DEST_CLIENT_NUMBER = CRL.DEST_INSTITUTION_NUMBER
        AND DET.CLIENT_TYPE IN (SELECT COLUMN_VALUE FROM TABLE (BW_UTIL.SPLIT( v_clientTypes, ',')))
        AND DET.CLIENT_NUMBER = CRL.CLIENT_NUMBER
        AND BCTYPE.INSTITUTION_NUMBER(+) = DET.INSTITUTION_NUMBER
        AND BCTYPE.INDEX_FIELD(+) = DET.CLIENT_TYPE
        AND BCTYPE.LANGUAGE(+) = 'USA'
        AND CRL.EFFECTIVE_DATE = (SELECT MAX(EFFECTIVE_DATE) FROM  CIS_CLIENT_RELATION_LINKS
                                  WHERE CLIENT_NUMBER = CRL.CLIENT_NUMBER
                                  AND INSTITUTION_NUMBER = CRL.INSTITUTION_NUMBER
                                  AND GROUP_NUMBER = CRL.GROUP_NUMBER
                                  AND LINK_TYPE = CRL.LINK_TYPE)
        CONNECT BY NOCYCLE
        PRIOR CRL.CLIENT_NUMBER = CRL.DEST_CLIENT_NUMBER
        START WITH DEST_CLIENT_NUMBER = CRL.INSTITUTION_NUMBER -- and prior crl.dest_client_number = '10000045' --and (crl.dest_client_number = 10000045  or

      GROUP BY CRL.EFFECTIVE_DATE,
               LIC.INSTITUTION_NUMBER,
               LIC.INSTITUTION_NAME,
               CRL.CLIENT_NUMBER,
               BCTYPE.CLIENT_TYPE,
               CRL.DEST_CLIENT_NUMBER,
               DET.TRADE_NAME,
               CRL.GROUP_NUMBER,
               DET.CLIENT_TYPE,
               DET.CLIENT_STATUS
      UNION ALL -- Query to link the merchant from cis_client_relation_links to Cis_client_Details (using cis_client_links to link by group number)

      SELECT MAX(CRL.EFFECTIVE_DATE) AS EFFECTIVE_DATE,
        LIC.INSTITUTION_NUMBER,
        LIC.INSTITUTION_NAME,
        DET.CLIENT_NUMBER,
        BCTYPE.CLIENT_TYPE,
        ---------------------------------

        CASE(CCL.PARENT_CLIENT_NUMBER)
          WHEN p_institution_number
            THEN CRL.DEST_CLIENT_NUMBER
            ELSE CCL.PARENT_CLIENT_NUMBER
        END AS DEST_CLIENT_NUMBER,
        ---------------------------------

        DET.TRADE_NAME,
        CRL.GROUP_NUMBER,
        CCL.CLIENT_LEVEL AS CLIENT_LEVEL,
        DET.CLIENT_TYPE AS CLIENT_TYPE_INDEX,
        DET.CLIENT_STATUS
      FROM SYS_INSTITUTION_LICENCE LIC,
           CIS_CLIENT_DETAILS DET,
           CIS_CLIENT_RELATION_LINKS CRL,
           CIS_CLIENT_LINKS CCL,
           BWT_CLIENT_TYPE BCTYPE
      WHERE LIC.INSTITUTION_NUMBER = CRL.INSTITUTION_NUMBER
        AND lic.institution_number = p_institution_number
        AND DET.INSTITUTION_NUMBER = CRL.INSTITUTION_NUMBER
        AND CRL.INSTITUTION_NUMBER = CRL.DEST_INSTITUTION_NUMBER
        AND CRL.DEST_CLIENT_NUMBER <> CRL.DEST_INSTITUTION_NUMBER
        AND CRL.GROUP_NUMBER = CCL.GROUP_NUMBER
        AND CRL.INSTITUTION_NUMBER = CCL.INSTITUTION_NUMBER
        AND CCL.CLIENT_NUMBER = DET.CLIENT_NUMBER
        AND CCL.INSTITUTION_NUMBER = DET.INSTITUTION_NUMBER
        AND CCL.CLIENT_NUMBER <> CCL.PARENT_CLIENT_NUMBER
        AND BCTYPE.INSTITUTION_NUMBER(+) = DET.INSTITUTION_NUMBER
        AND BCTYPE.INDEX_FIELD(+) = DET.CLIENT_TYPE
        AND BCTYPE.LANGUAGE(+) = 'USA'
        AND CCL.EFFECTIVE_DATE =
          (SELECT MAX(EFFECTIVE_DATE)
           FROM CIS_CLIENT_LINKS
           WHERE GROUP_NUMBER = CCL.GROUP_NUMBER
           AND INSTITUTION_NUMBER = CCL.INSTITUTION_NUMBER
           AND CLIENT_NUMBER = CCL.CLIENT_NUMBER
           AND SERVICE_CONTRACT_ID = CCL.SERVICE_CONTRACT_ID
           AND CLIENT_LEVEL = CCL.CLIENT_LEVEL)
        AND DET.CLIENT_TYPE IN (SELECT COLUMN_VALUE FROM TABLE (BW_UTIL.SPLIT( v_clientTypes, ',')))
        AND DET.CLIENT_STATUS = '001'
        AND CRL.EFFECTIVE_DATE = (SELECT MAX(EFFECTIVE_DATE) FROM  CIS_CLIENT_RELATION_LINKS
                                  WHERE CLIENT_NUMBER = CRL.CLIENT_NUMBER
                                  AND INSTITUTION_NUMBER = CRL.INSTITUTION_NUMBER
                                  AND GROUP_NUMBER = CRL.GROUP_NUMBER
                                  AND LINK_TYPE = CRL.LINK_TYPE)
        CONNECT BY NOCYCLE
        PRIOR CRL.CLIENT_NUMBER = CRL.DEST_CLIENT_NUMBER
      GROUP BY LIC.INSTITUTION_NUMBER,
               LIC.INSTITUTION_NAME,
               DET.CLIENT_NUMBER,
               BCTYPE.CLIENT_TYPE,
               DET.TRADE_NAME,
               CRL.GROUP_NUMBER,
               CCL.CLIENT_LEVEL,
               DET.CLIENT_TYPE,
               DET.CLIENT_STATUS,
               CASE(CCL.PARENT_CLIENT_NUMBER)
                WHEN p_institution_number
                  THEN CRL.DEST_CLIENT_NUMBER
                  ELSE CCL.PARENT_CLIENT_NUMBER
                END
   ) hrc
   START WITH hrc.DEST_CLIENT_NUMBER = p_institution_number CONNECT BY NOCYCLE
   PRIOR hrc.CLIENT_NUMBER = hrc.DEST_CLIENT_NUMBER
   AND PRIOR hrc.CLIENT_NUMBER <> PRIOR hrc.DEST_CLIENT_NUMBER  )
   WHERE  CBP LIKE decode(p_client_number, null, '/%','/'||p_client_number||'%')

  ) loop
    --

    pipe row(row_iso(rec.CLIENT_NUMBER, rec.DEST_CLIENT_NUMBER, rec.CLIENT_LEVEL, rec.CLIENT_TYPE, rec.CLIENT_TYPE_INDEX,
    rec.INSTITUTION_NUMBER,  rec.INSTITUTION_NAME, rec.TRADE_NAME,  rec.GROUP_NUMBER, '',
    0, rec.CBP, rec.CLIENT_STATUS
    )); -- Generator Type - Return result without yielding control
    --

  end loop;
exception
  when others then
    raise_application_error(-20022, 'Error in proc_iso_hierarchy_rpt:'||sqlerrm||dbms_utility.format_error_backtrace);
end proc_iso_hierarchy_rpt;