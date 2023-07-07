FUNCTION GET_VIRTUAL_HIERARCHY_CLIENT (
  p_inst_num        IN VARCHAR2,
  p_client_num      IN VARCHAR2,
  p_group_num       IN VARCHAR2,
  p_top_client_type IN VARCHAR2,
  p_business_type   IN VARCHAR2
)
RETURN VARCHAR2
/*
 Function GET_VIRTUAL_HIERARCHY_CLIENT returns the Institution_Number concatenated to the top-most Client Number in the virtual hierarchy that has the Client_Type = p_top_client_type.
 The Business Type must be used to traverse the intended virtual hierarchy.
 To further ensure that the correct hierarchy is traversed, the sub query STARTING_LINK_TYPES gets all possible starting link types that ultimately lead to the target top_client_type.
 Note that it is possible to have clients with group number 99999999 in CIS_CLIENT_RELATION_LINKS.
*/
IS
    v_TopClient        CIS_CLIENT_RELATION_LINKS.DEST_CLIENT_NUMBER%TYPE;
    v_TopInstNo        CIS_CLIENT_RELATION_LINKS.DEST_INSTITUTION_NUMBER%TYPE;
    v_TopClientInstNo  VARCHAR2(16);

BEGIN
    --
	FOR rec IN (
	            with   STARTING_LINK_TYPES as
					(
						select     distinct index_field
						from       bwt_link_type
						start with institution_number = p_inst_num
						and        destination_client_type = p_top_client_type
						and        language = 'USA'
						connect by destination_client_type = prior source_client_type
						and        institution_number = prior institution_number
						and        source_client_type <> prior source_client_type
						and        language = 'USA'
					)
	            SELECT CRL.DEST_CLIENT_NUMBER, CRL.DEST_INSTITUTION_NUMBER
				FROM   CIS_CLIENT_RELATION_LINKS CRL,
				       CIS_CLIENT_DETAILS CD
				WHERE  CRL.LINK_STATUS = '001'
				AND    CD.INSTITUTION_NUMBER = CRL.INSTITUTION_NUMBER
				AND    CD.CLIENT_NUMBER = CRL.DEST_CLIENT_NUMBER
				AND    CD.CLIENT_TYPE = p_top_client_type
				START WITH CRL.INSTITUTION_NUMBER = p_inst_num
				AND   CRL.CLIENT_NUMBER = p_client_num
				AND   CRL.GROUP_NUMBER in (p_group_num, '99999999')
				AND   CRL.LINK_TYPE in (select * from STARTING_LINK_TYPES)
				CONNECT BY CRL.CLIENT_NUMBER      = PRIOR DEST_CLIENT_NUMBER
				AND        CRL.INSTITUTION_NUMBER = PRIOR DEST_INSTITUTION_NUMBER
				AND        (select clnt.business_type
							from   bwt_link_type   lnkt,
							       bwt_client_type clnt
							where  lnkt.institution_number = CRL.INSTITUTION_NUMBER
							and    lnkt.index_field = CRL.link_type
							and    lnkt.language = 'USA'
							and    clnt.institution_number = lnkt.institution_number
							and    clnt.index_field = lnkt.destination_client_type
							and    clnt.language = 'USA') = p_business_type
				)
    LOOP
      --
      v_TopClient       := rec.DEST_CLIENT_NUMBER;
      v_TopInstNo       := rec.DEST_INSTITUTION_NUMBER;
      v_TopClientInstNo := v_TopInstNo || v_TopClient;
      --
    END LOOP;
    --
    RETURN v_TopClientInstNo;
    --
EXCEPTION
    WHEN OTHERS THEN
        RETURN NULL;
END GET_VIRTUAL_HIERARCHY_CLIENT;