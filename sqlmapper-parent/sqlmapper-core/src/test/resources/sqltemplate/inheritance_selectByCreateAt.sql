select * from test_inheritance
	/*BEGIN*/
	where
		/*IF createAt != null*/
		create_at >= /*createAt*/
		/*END*/
	/*END*/