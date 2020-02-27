-- ignore-cols: foo, bar, monkey-- ignore-cols: foo, bar, monkey

-- include-diff-col: foo
select id, username
--
from user_account limit 1;
