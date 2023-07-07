select
  "alias_39173775"."INSTITUTION_NUMBER",
  "alias_39173775"."CLIENT_NUMBER",
  "alias_39173775"."CLIENT_LEVEL",
  "alias_39173775"."GROUP_NUMBER",
  "alias_39173775"."SERVICE_CONTRACT_ID",
  "alias_39173775"."CONTACT_NAME",
  "alias_39173775"."CLIENT_TYPE",
  "alias_39173775"."LEVEL",
  "alias_39173775"."PATH",
  "alias_39173775"."PATHCLIENTNAME",
  "alias_39173775"."PATHCLIENTLEVEL"
from (
  select
    "alias_8615564"."INSTITUTION_NUMBER",
    "alias_8615564"."CLIENT_NUMBER",
    "alias_8615564"."CLIENT_LEVEL",
    "alias_8615564"."GROUP_NUMBER",
    "alias_8615564"."SERVICE_CONTRACT_ID",
    "alias_8615564"."CONTACT_NAME",
    "alias_8615564"."CLIENT_TYPE",
    "alias_8615564"."LEVEL",
    "alias_8615564"."PATH",
    "alias_8615564"."PATHCLIENTNAME",
    "alias_8615564"."PATHCLIENTLEVEL"
  from (
    select
      "BW3"."CIS_CLIENT_LINKS"."INSTITUTION_NUMBER",
      "BW3"."CIS_CLIENT_LINKS"."CLIENT_NUMBER",
      "BW3"."CIS_CLIENT_LINKS"."CLIENT_LEVEL",
      "BW3"."CIS_CLIENT_LINKS"."GROUP_NUMBER",
      "BW3"."CIS_CLIENT_LINKS"."SERVICE_CONTRACT_ID",
      "BW3"."CIS_CLIENT_DETAILS"."CONTACT_NAME",
      "BW3"."CIS_CLIENT_DETAILS"."CLIENT_TYPE",
      level "LEVEL",
      sys_connect_by_path(
        "BW3"."CIS_CLIENT_LINKS"."CLIENT_NUMBER",
        '/'
      ) "PATH",
      sys_connect_by_path(
        "BW3"."CIS_CLIENT_DETAILS"."CONTACT_NAME",
        '/'
      ) "PATHCLIENTNAME",
      sys_connect_by_path(
        "BW3"."CIS_CLIENT_LINKS"."CLIENT_LEVEL",
        '/'
      ) "PATHCLIENTLEVEL"
    from "BW3"."CIS_CLIENT_LINKS"
      join "BW3"."CIS_CLIENT_DETAILS"
        on (
          "BW3"."CIS_CLIENT_LINKS"."CLIENT_NUMBER" = "BW3"."CIS_CLIENT_DETAILS"."CLIENT_NUMBER"
          and "BW3"."CIS_CLIENT_LINKS"."INSTITUTION_NUMBER" = "BW3"."CIS_CLIENT_DETAILS"."INSTITUTION_NUMBER"
        )
    where (
      "BW3"."CIS_CLIENT_LINKS"."CONTRACT_CATEGORY" in (
        "BW3"."BWTPAD"(
          'BWT_CONTRACT_CATEGORY',
          '001'
        ), "BW3"."BWTPAD"(
          'BWT_CONTRACT_CATEGORY',
          '003'
        )
      )
      and "BW3"."CIS_CLIENT_LINKS"."CLIENT_NUMBER" <> "BW3"."CIS_CLIENT_LINKS"."PARENT_CLIENT_NUMBER"
      and "BW3"."CIS_CLIENT_LINKS"."EFFECTIVE_DATE" = (
        select max("subccl"."EFFECTIVE_DATE")
        from "BW3"."CIS_CLIENT_LINKS" "subccl"
        where (
          "subccl"."INSTITUTION_NUMBER" = "BW3"."CIS_CLIENT_LINKS"."INSTITUTION_NUMBER"
          and "subccl"."INSTITUTION_NUMBER" = '&institution_number'
          and "subccl"."CLIENT_NUMBER" = "BW3"."CIS_CLIENT_LINKS"."CLIENT_NUMBER"
          and "subccl"."GROUP_NUMBER" = "BW3"."CIS_CLIENT_LINKS"."GROUP_NUMBER"
          and "subccl"."SERVICE_CONTRACT_ID" = "BW3"."CIS_CLIENT_LINKS"."SERVICE_CONTRACT_ID"
          and "subccl"."CLIENT_LEVEL" = "BW3"."CIS_CLIENT_LINKS"."CLIENT_LEVEL"
          and "subccl"."EFFECTIVE_DATE" <= '20230522'
        )
      )
      and "BW3"."CIS_CLIENT_LINKS"."CONTRACT_STATUS" <> '004'
      and "BW3"."CIS_CLIENT_LINKS"."CLIENT_NUMBER" in (
        select "alias_95916304"."CLIENT_NUMBER"
        from (
          select
            "mat"."TITLE",
            "mat"."SHORT_NAME",
            "mat"."LEGAL_FORM",
            "mat"."GROUP_NUMBER",
            "mat"."CLIENT_LEVEL",
            "mat"."RECORD_TYPE",
            "mat"."RECORD_DATE",
            "mat"."AUDIT_TRAIL",
            "mat"."CONTACT_NAME",
            "mat"."CLIENT_STATUS",
            "mat"."CLIENT_BRANCH",
            "mat"."BUSINESS_CLASS",
            "mat"."VAT_REG_NUMBER",
            "mat"."CLIENT_LANGUAGE",
            "mat"."CLIENT_NUMBER_RBS",
            "mat"."WEBSITE_ADDRESS",
            "mat"."ECOMMERCE_INDICATOR",
            "mat"."CLIENT_REGION",
            "mat"."PARENT_CLIENT_NUMBER",
            "mat"."CLIENT_NUMBER",
            "mat"."INSTITUTION_NUMBER",
            "mat"."TEL_PRIVATE",
            "mat"."TEL_WORK",
            "mat"."REGISTRATION_NUMBER",
            "mat"."COMPANY_NAME",
            "mat"."TRADE_NAME",
            "mat"."OUR_REFERENCE",
            "mat"."MOBILE_NO_1",
            "mat"."MOBILE_NO_2",
            "mat"."SERVICE_CONTRACT_ID",
            "mat"."CLIENT_TYPE"
          from (
            select
              "dtl"."TITLE",
              "dtl"."SHORT_NAME",
              "dtl"."LEGAL_FORM",
              "dtl"."GROUP_NUMBER",
              "dtl"."CLIENT_LEVEL",
              "dtl"."RECORD_TYPE",
              "dtl"."RECORD_DATE",
              "dtl"."AUDIT_TRAIL",
              "dtl"."CONTACT_NAME",
              "dtl"."CLIENT_STATUS",
              "dtl"."CLIENT_BRANCH",
              "dtl"."BUSINESS_CLASS",
              "dtl"."VAT_REG_NUMBER",
              "dtl"."CLIENT_LANGUAGE",
              "dtl"."CLIENT_NUMBER_RBS",
              "dtl"."WEBSITE_ADDRESS",
              "dtl"."ECOMMERCE_INDICATOR",
              "dtl"."CLIENT_REGION",
              "dtl"."PARENT_CLIENT_NUMBER",
              "dtl"."CLIENT_NUMBER",
              "dtl"."INSTITUTION_NUMBER",
              "dtl"."TEL_PRIVATE",
              "dtl"."TEL_WORK",
              "dtl"."REGISTRATION_NUMBER",
              "dtl"."COMPANY_NAME",
              "dtl"."TRADE_NAME",
              "dtl"."OUR_REFERENCE",
              "dtl"."MOBILE_NO_1",
              "dtl"."MOBILE_NO_2",
              "dtl"."SERVICE_CONTRACT_ID",
              "dtl"."CLIENT_TYPE"
            from "BW3"."MAT_BRANDING_SEARCH" "dtl"
            where (
              "dtl"."INSTITUTION_NUMBER" = '&institution_number'
              and "dtl"."BRAND_ID" = (select distinct brand_id from MAT_BRANDING_SEARCH  where institution_number = '&institution_number' and client_number = '&client_number')
            )
          ) "mat"
        ) "alias_95916304"
      )
      and "BW3"."CIS_CLIENT_LINKS"."CLIENT_NUMBER" <> '&client_number'
    )
    start with (
      "BW3"."CIS_CLIENT_LINKS"."CLIENT_NUMBER" = '&client_number'
     and "BW3"."CIS_CLIENT_LINKS"."PARENT_CLIENT_NUMBER" in (
        select "BW3"."CIS_CLIENT_LINKS"."PARENT_CLIENT_NUMBER"
        from "BW3"."CIS_CLIENT_LINKS"
        where (
          "BW3"."CIS_CLIENT_LINKS"."CLIENT_NUMBER" = '&client_number'
          and "BW3"."CIS_CLIENT_LINKS"."CLIENT_NUMBER" <> "BW3"."CIS_CLIENT_LINKS"."PARENT_CLIENT_NUMBER"

          and "BW3"."CIS_CLIENT_LINKS"."INSTITUTION_NUMBER" = '&institution_number'
        )
      )
    )
    connect by nocycle (
      prior "BW3"."CIS_CLIENT_LINKS"."CLIENT_NUMBER" = "BW3"."CIS_CLIENT_LINKS"."PARENT_CLIENT_NUMBER"
      and prior "BW3"."CIS_CLIENT_LINKS"."GROUP_NUMBER" = "BW3"."CIS_CLIENT_LINKS"."GROUP_NUMBER"
      and prior "BW3"."CIS_CLIENT_LINKS"."PARENT_CLIENT_NUMBER" <> "BW3"."CIS_CLIENT_LINKS"."PARENT_CLIENT_NUMBER"
    )
    order by
      "BW3"."CIS_CLIENT_LINKS"."GROUP_NUMBER",
      level

  ) "alias_8615564"
) "alias_39173775"
where "alias_39173775"."INSTITUTION_NUMBER" = '&institution_number'
