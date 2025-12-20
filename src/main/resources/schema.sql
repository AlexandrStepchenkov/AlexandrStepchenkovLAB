create table if not exists users (
  user_id   int primary key,
  name      varchar(100) not null,
  email     varchar(100) unique not null,
  phone     varchar(20) unique not null
);

create table if not exists cars (
  car_id        int primary key,
  model         varchar(100) not null,
  license_plate varchar(20) unique not null,
  price_per_km  decimal(10,2) not null check (price_per_km > 0),
  available     boolean not null default true
);

create table if not exists rides (
  ride_id       bigserial primary key,
  user_id       int not null references users(user_id) on delete cascade,
  car_id        int not null references cars(car_id) on delete restrict,
  distance_km   decimal(10,2) not null check (distance_km > 0),
  duration_hours decimal(8,2) not null check (duration_hours > 0),
  status        varchar(20) not null check (status in ('CREATED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
  created_at    timestamp default current_timestamp,

  constraint uq_active_ride unique (car_id)
    where status = 'IN_PROGRESS'
);

create table if not exists payments (
  payment_id    bigserial primary key,
  ride_id       bigint unique not null references rides(ride_id) on delete cascade,
  amount        decimal(10,2) not null check (amount >= 0),
  created_at    timestamp default current_timestamp
);

create index if not exists idx_ride_user on rides(user_id);
create index if not exists idx_ride_car on rides(car_id);
create index if not exists idx_ride_status on rides(status);
create index if not exists idx_car_available on cars(available) where available = true;
create index if not exists idx_payment_ride on payments(ride_id);