select * from customer
	/*BEGIN*/
	where
		/*IF lastName != null*/
		last_name = /*lastName*/'Test'
		/*END*/
	/*END*/
	order by customer_id
