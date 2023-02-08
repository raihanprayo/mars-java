create table t_sto
(
    id    serial primary key,
    witel varchar(20)       not null,
    datel varchar(50)       not null,
    alias varchar(5) unique not null,
    name  varchar(50)
);

insert into t_sto (witel, datel, alias, name)
VALUES ('BANTEN', 'BANTEN', 'BJT', 'BOJOT'),
       ('BANTEN', 'BANTEN', 'BRS', 'BAROS'),
       ('BANTEN', 'BANTEN', 'CKD', 'CIKANDE'),
       ('BANTEN', 'BANTEN', 'CRS', 'CIRUAS'),
       ('BANTEN', 'BANTEN', 'KMT', 'KRAMAT WATU'),
       ('BANTEN', 'BANTEN', 'SEG', 'SERANG'),
       ('BANTEN', 'CIKUPA', 'BLJ', 'BALARAJA'),
       ('BANTEN', 'CIKUPA', 'CKA', 'CIKUPA'),
       ('BANTEN', 'CIKUPA', 'CSK', 'CISOKA'),
       ('BANTEN', 'CIKUPA', 'KRS', 'KRESEK'),
       ('BANTEN', 'CIKUPA', 'SAG', 'SAGA'),
       ('BANTEN', 'CIKUPA', 'TGR', 'TIGA RAKSA'),
       ('BANTEN', 'CIKUPA', 'TJO', 'TENJO'),
       ('BANTEN', 'CILEGON', 'BJO', 'BOJONAGARA'),
       ('BANTEN', 'CILEGON', 'CLG', 'CILEGON'),
       ('BANTEN', 'CILEGON', 'CWN', 'CIWANDAN'),
       ('BANTEN', 'CILEGON', 'GRL', 'GEROGOL'),
       ('BANTEN', 'CILEGON', 'MER', 'MERAK'),
       ('BANTEN', 'CILEGON', 'PBN', 'PABEAN'),
       ('BANTEN', 'CILEGON', 'PSU', 'PASAURAN'),
       ('BANTEN', 'CILEGON', 'SAM', 'SAMANG RAYA'),
       ('BANTEN', 'RANGKAS-PANDEGLANG', 'BAY', 'BAYAH'),
       ('BANTEN', 'RANGKAS-PANDEGLANG', 'LBU', 'LABUAN'),
       ('BANTEN', 'RANGKAS-PANDEGLANG', 'LWD', 'LEUWIDAMAR'),
       ('BANTEN', 'RANGKAS-PANDEGLANG', 'MEN', 'MENES'),
       ('BANTEN', 'RANGKAS-PANDEGLANG', 'MLP', 'MALINGPING'),
       ('BANTEN', 'RANGKAS-PANDEGLANG', 'PDG', 'PANDEGLANG'),
       ('BANTEN', 'RANGKAS-PANDEGLANG', 'RKS', 'RANGKASBITUNG'),
       ('BANTEN', 'RANGKAS-PANDEGLANG', 'SKE', 'SAKETI'),
       ('BEKASI', 'BEKASI', 'BBL', 'BABELAN'),
       ('BEKASI', 'BEKASI', 'BEK', 'BEKASI'),
       ('BEKASI', 'BEKASI', 'KLB', 'KALIABANG'),
       ('BEKASI', 'BEKASI', 'KRA', 'KRANJI'),
       ('BEKASI', 'BEKASI', 'PDE', 'PONDOK GEDE'),
       ('BEKASI', 'BEKASI', 'PKY', 'PEKAYON'),
       ('BEKASI', 'BEKASI', 'TAR', 'TARUMAJAYA'),
       ('BEKASI', 'CIKARANG', 'BGG', 'BANTAR GEBANG'),
       ('BEKASI', 'CIKARANG', 'CBG', 'CABANGBUNGIN'),
       ('BEKASI', 'CIKARANG', 'CBR', 'CIBARUSA'),
       ('BEKASI', 'CIKARANG', 'CIB', 'CIBITUNG'),
       ('BEKASI', 'CIKARANG', 'CIK', 'CIKARANG'),
       ('BEKASI', 'CIKARANG', 'DNI', 'MDF DANAU INDAH'),
       ('BEKASI', 'CIKARANG', 'EJI', 'EJIP'),
       ('BEKASI', 'CIKARANG', 'JBB', 'JABABEKA'),
       ('BEKASI', 'CIKARANG', 'LMA', 'LEMAH ABANG'),
       ('BEKASI', 'CIKARANG', 'MGB', 'MUARA GEMBONG'),
       ('BEKASI', 'CIKARANG', 'PBY', 'PEBAYURAN'),
       ('BEKASI', 'CIKARANG', 'SMH', 'SUKAMAHI'),
       ('BEKASI', 'CIKARANG', 'STN', 'SUKATANI'),
       ('BEKASI', 'CIKARANG', 'SUE', 'SUKARESMI'),
       ('BEKASI', 'CIKARANG', 'TBL', 'TAMBELANG'),
       ('BOGOR', 'CIBINONG', 'BJD', 'BOJONG GEDE'),
       ('BOGOR', 'CIBINONG', 'CAU', 'CARIU'),
       ('BOGOR', 'CIBINONG', 'CBI', 'CIBINONG'),
       ('BOGOR', 'CIBINONG', 'CLS', 'CILEUNGSI'),
       ('BOGOR', 'CIBINONG', 'CSN', 'CIANGSANA'),
       ('BOGOR', 'CIBINONG', 'GPI', 'GUNUNG PUTRI'),
       ('BOGOR', 'CIBINONG', 'JGL', 'JONGGOL'),
       ('BOGOR', 'CIBINONG', 'TJH', 'TAJUR HALANG'),
       ('BOGOR', 'DEPOK', 'CNE', 'CINERE'),
       ('BOGOR', 'DEPOK', 'CSL', 'CISALAK'),
       ('BOGOR', 'DEPOK', 'DEP', 'DEPOK'),
       ('BOGOR', 'DEPOK', 'PCM', 'PANCORAN MAS'),
       ('BOGOR', 'DEPOK', 'SKJ', 'SUKMAJAYA'),
       ('BOGOR', 'KUJANG', 'BOO', 'BOGOR'),
       ('BOGOR', 'KUJANG', 'CGD', 'CIGUDEG'),
       ('BOGOR', 'KUJANG', 'CJU', 'CIJERUK'),
       ('BOGOR', 'KUJANG', 'CPS', 'CIAPUS'),
       ('BOGOR', 'KUJANG', 'CSE', 'CISEENG'),
       ('BOGOR', 'KUJANG', 'DMG', 'DRAMAGA'),
       ('BOGOR', 'KUJANG', 'JSA', 'JASINGA'),
       ('BOGOR', 'KUJANG', 'LBI', 'LEBAK WANGI'),
       ('BOGOR', 'KUJANG', 'LWL', 'LEUWILIANG'),
       ('BOGOR', 'KUJANG', 'PAG', 'PAGELARAN'),
       ('BOGOR', 'KUJANG', 'PAR', 'STO PARUNG'),
       ('BOGOR', 'KUJANG', 'SPL', 'SEMPLAK'),
       ('BOGOR', 'SENTUL', 'CRI', 'CARINGIN'),
       ('BOGOR', 'SENTUL', 'CSR', 'CISARUA'),
       ('BOGOR', 'SENTUL', 'CTR', 'CITEUREUP'),
       ('BOGOR', 'SENTUL', 'CWI', 'CIAWI'),
       ('BOGOR', 'SENTUL', 'KHL', 'KEDUNG HALANG'),
       ('BOGOR', 'SENTUL', 'PMU', 'PASIR MAUNG'),
       ('BOGOR', 'SENTUL', 'STL', 'SENTUL'),
       ('JAKBAR', 'JAKBAR', 'CKG', 'CENGKARENG'),
       ('JAKBAR', 'JAKBAR', 'JIA', 'JAKARTA INTERNATIONAL AIRPORT'),
       ('JAKBAR', 'JAKBAR', 'KDY', 'KEDOYA'),
       ('JAKBAR', 'JAKBAR', 'KPK', 'KAPUK'),
       ('JAKBAR', 'JAKBAR', 'KSB', 'KOSAMBI'),
       ('JAKBAR', 'JAKBAR', 'MRY', 'MERUYA'),
       ('JAKBAR', 'JAKBAR', 'PLM', 'PALMERAH'),
       ('JAKBAR', 'JAKBAR', 'SDM', 'SLIPI DUTA MAS'),
       ('JAKBAR', 'JAKBAR', 'SLP', 'SLIPI'),
       ('JAKBAR', 'JAKBAR', 'SMI', 'SEMANGGI'),
       ('JAKBAR', 'JAKBAR', 'TGA', 'TEGAL ALUR'),
       ('JAKPUS', 'JAKPUS', 'CID', 'CIDENG'),
       ('JAKPUS', 'JAKPUS', 'CPP', 'CEMPAKA PUTIH'),
       ('JAKPUS', 'JAKPUS', 'GBC', 'CIKINI'),
       ('JAKPUS', 'JAKPUS', 'GBI', 'GAMBIR'),
       ('JAKPUS', 'JAKPUS', 'KMY', 'KEMAYORAN'),
       ('JAKSEL', 'JAKSEL', 'BIN', 'BINTARO'),
       ('JAKSEL', 'JAKSEL', 'CPE', 'CIPETE'),
       ('JAKSEL', 'JAKSEL', 'JAG', 'JAGAKARSA'),
       ('JAKSEL', 'JAKSEL', 'KAL', 'KALIBATA'),
       ('JAKSEL', 'JAKSEL', 'KBY', 'KEBAYORAN'),
       ('JAKSEL', 'JAKSEL', 'KMG', 'KEMANG'),
       ('JAKSEL', 'JAKSEL', 'PSM', 'PASAR MINGGU'),
       ('JAKSEL', 'JAKSEL', 'TBE', 'TEBET'),
       ('JAKTIM', 'JAKTIM', 'CBB', 'CIBUBUR'),
       ('JAKTIM', 'JAKTIM', 'CWA', 'CAWANG'),
       ('JAKTIM', 'JAKTIM', 'GAN', 'GANDARIA'),
       ('JAKTIM', 'JAKTIM', 'JTN', 'JATINEGARA'),
       ('JAKTIM', 'JAKTIM', 'KLD', 'KLENDER'),
       ('JAKTIM', 'JAKTIM', 'KRG', 'KRANGGAN'),
       ('JAKTIM', 'JAKTIM', 'PDK', 'PONDOK KELAPA'),
       ('JAKTIM', 'JAKTIM', 'PGB', 'PULO GEBANG'),
       ('JAKTIM', 'JAKTIM', 'PGG', 'PENGGILINGAN'),
       ('JAKTIM', 'JAKTIM', 'PSR', 'PASAR REBO'),
       ('JAKTIM', 'JAKTIM', 'RMG', 'RAWAMANGUN'),
       ('JAKUT', 'JAKUT', 'CIL', 'CILINCING'),
       ('JAKUT', 'JAKUT', 'KLG', 'KELAPA GADING'),
       ('JAKUT', 'JAKUT', 'KTX', 'KOTA'),
       ('JAKUT', 'JAKUT', 'KTZ', 'MANGGA BESAR'),
       ('JAKUT', 'JAKUT', 'MKR', 'MUARA KARANG'),
       ('JAKUT', 'JAKUT', 'MRD', 'MARUNDA'),
       ('JAKUT', 'JAKUT', 'PDM', 'MDF Pademangan'),
       ('JAKUT', 'JAKUT', 'STR', 'SUNTER'),
       ('JAKUT', 'JAKUT', 'TPR', 'TANJUNG PRIUK'),
       ('TANGERANG', 'CIPUTAT', 'CLD', 'MDF CDG'),
       ('TANGERANG', 'CIPUTAT', 'CPA', 'CIPUTAT'),
       ('TANGERANG', 'CIPUTAT', 'PDR', 'PONDOK AREN'),
       ('TANGERANG', 'CIPUTAT', 'PKU', 'PAKULONAN'),
       ('TANGERANG', 'CIPUTAT', 'SRH', 'SERUA INDAH'),
       ('TANGERANG', 'LENGKONG', 'CUG', 'CURUG'),
       ('TANGERANG', 'LENGKONG', 'LGK', 'LEGOK'),
       ('TANGERANG', 'LENGKONG', 'LKG', 'LENGKONG'),
       ('TANGERANG', 'LENGKONG', 'PPG', 'PARUNG PANJANG'),
       ('TANGERANG', 'LENGKONG', 'RMP', 'RUMPIN'),
       ('TANGERANG', 'LENGKONG', 'SRP', 'SERPONG'),
       ('TANGERANG', 'PASAR BARU', 'CKL', 'MDF CIKOKOL'),
       ('TANGERANG', 'PASAR BARU', 'CPD', 'CIPONDOH'),
       ('TANGERANG', 'PASAR BARU', 'GDS', 'GANDA SARI'),
       ('TANGERANG', 'PASAR BARU', 'KJO', 'KRONYO'),
       ('TANGERANG', 'PASAR BARU', 'MUK', 'MAUK'),
       ('TANGERANG', 'PASAR BARU', 'PSK', 'PASAR KAMIS'),
       ('TANGERANG', 'PASAR BARU', 'RJG', 'RAJEG'),
       ('TANGERANG', 'PASAR BARU', 'SPT', 'SEPATAN'),
       ('TANGERANG', 'PASAR BARU', 'TAN', 'TANGERANG');