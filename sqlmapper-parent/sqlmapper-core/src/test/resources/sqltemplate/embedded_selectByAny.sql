select * from TEST_EMBEDDED
	/*BEGIN*/
	where
		/*IF id.key1 != null*/
		key1 = /*id.key1*/'010'
		/*END*/
		/*IF id.key2 != null*/
		and key2 = /*id.key2*/3
		/*END*/
		/*IF name != null*/
		and lower(name) like /*name*/'%3%'
		/*END*/
	/*END*/
	order by key1, key2
