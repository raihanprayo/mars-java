update t_user as usr
set name  = upper(trim(BOTH ' ' FROM usr.name)),
    phone = trim(BOTH ' ' FROM usr.phone),
    nik = trim(BOTH ' ' from usr.nik)