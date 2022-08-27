# TODO fix generator
import string
import requests
import random
import datetime
import time

from ordered_set import OrderedSet

MIN_NAME_LENGTH = 3
MAX_NAME_LENGTH = 50
MIN_DESC_LENGTH = 10
MAX_DESC_LENGTH = 100
MIN_PRICE = 1.0
MAX_PRICE = 100.0
MIN_DURATION = 1
MAX_DURATION = 60
MIN_DATE = '01-01-2019 00:00:00'
MAX_DATE = '31-12-2020 23:59:59'
MIN_USERNAME_LENGTH = 8
MAX_USERNAME_LENGTH = 32

MIN_TAGS_PER_CERTIFICATE = 0
MAX_TAGS_PER_CERTIFICATE = 3

MIN_CERTIFICATES_PER_ORDER = 1
MAX_CERTIFICATES_PER_ORDER = 3

def fetch_word(min_length, max_length):
    ''' 
    Fetch a word that matches specified length boundaries 
    '''

    while True:
        # call an external API
        try:
            r = requests.get('https://random-word-api.herokuapp.com/word?number=1&swear=0')
            word = r.json()[0]
        except:
            time.sleep(2)
            continue

        if len(word) >= min_length and len(word) <= max_length:
            break

    return word

def generate_date(min_date, max_date):
    """
    Generate a random datetime in specified range
    """

    generated_date = '%d-%m-%Y %H:%M:%S'
    start = datetime.datetime.strptime(min_date, generated_date)
    end = datetime.datetime.strptime(max_date, generated_date)
    delta = end - start
    return random.random() * delta + start

def generate_insert(table_name, cols_vals):   
    '''
    Generate INSERT SQL statement
    '''

    statement = string.Template(f'INSERT INTO {table_name} ($columns) VALUES ($values);')
    columns = []
    values = []

    for name, value in cols_vals.items():
        columns.append(name)
        values.append(f"'{value}'")

    columns_statement = ', '.join(columns)
    values_statement = ', '.join(values)

    return statement.substitute(
        columns=columns_statement, 
        values=values_statement
    )

def dump(file_name, lines):
    with open(file_name, 'w') as file:
        file.writelines(lines)

def generate_data(file_name, certificates_total, tags_total, users_total, orders_total):
    '''
    Generate init data
    '''
    lines = OrderedSet()

    try:
        # certificates
        for i in range(certificates_total):
            name = fetch_word(MIN_NAME_LENGTH, MAX_NAME_LENGTH)
            description = fetch_word(MIN_DESC_LENGTH, MAX_DESC_LENGTH)
            price = random.uniform(MIN_PRICE, MAX_PRICE)
            duration = random.randrange(MIN_DURATION, MAX_DURATION)
            create_date = last_update_date = generate_date(MIN_DATE, MAX_DATE)

            certificate_statement = generate_insert('gift_certificate', {
                'name': name, 
                'description': description, 
                'price': price, 
                'duration': duration, 
                'create_date': create_date, 
                'last_update_date': last_update_date
            })

            lines.add(certificate_statement + '\n')
            print('Certificates generated: ', i + 1)

        # tags
        tags_generated = 0
        for i in range(tags_total):
            name = fetch_word(MIN_NAME_LENGTH, MAX_NAME_LENGTH)

            tag_statement = generate_insert('tag', {
                'name': name
            })

            if lines.add(tag_statement + '\n') == len(lines) - 1:
                tags_generated += 1
                print('Tags generated: ', tags_generated)

        # users
        users_generated = 0
        for i in range(users_total):
            username = fetch_word(MIN_USERNAME_LENGTH, MAX_USERNAME_LENGTH)
            password = '$2a$12$xKPnRQjvcaI7otzmjburzuIRu4kGqSXTkAwckVQBY4l7BI6XP1A8S'
            role = 0

            user_statement = generate_insert('app_user', {
                'username': username,
                'password': password,
                'role': role
            })

            if lines.add(user_statement + '\n') == len(lines) - 1:
                users_generated += 1
                print('Users generated: ', users_generated)

        # orders
        for i in range(orders_total):
            cost = random.uniform(MIN_PRICE, MAX_PRICE)
            purchase_date = generate_date(MIN_DATE, MAX_DATE)
            user_id = random.randrange(0, users_generated) + 1

            order_statement = generate_insert('app_order', {
                'cost': cost,
                'purchase_date': purchase_date,
                'id_user': user_id,
            })

            lines.add(order_statement + '\n')
            print('Orders generated: ', i + 1)

        # link tags to certificates
        max_tags = MAX_TAGS_PER_CERTIFICATE if tags_generated >= MAX_TAGS_PER_CERTIFICATE else tags_generated
        for i in range(certificates_total):
            certificate_id = i + 1
            tags_num = random.randrange(MIN_TAGS_PER_CERTIFICATE, max_tags)

            for _ in range(tags_num):
                tag_id = random.randrange(0, tags_generated) + 1
                
                certificate_tag_statement = generate_insert('certificate_tag', {
                    'id_certificate': certificate_id,
                    'id_tag': tag_id
                })

                lines.add(certificate_tag_statement + '\n')

        print('Tags successfully linked to certificates')

        # link certificates to orders
        max_certificates = MAX_CERTIFICATES_PER_ORDER if certificates_total >= MAX_CERTIFICATES_PER_ORDER else certificates_total
        for i in range(orders_total):
            order_id = i + 1
            certificates_num = random.randrange(MIN_CERTIFICATES_PER_ORDER, max_certificates)

            for _ in range(certificates_num):
                certificate_id = random.randrange(0, certificates_total) + 1

                certificate_order_statement = generate_insert('certificate_order', {
                    'id_order': order_id,
                    'id_certificate': certificate_id
                })

                lines.add(certificate_order_statement + '\n')

        print('Certificates successfully linked to orders')
    except:
        pass
    finally:
        dump(file_name, lines)

# execute
generate_data('data.sql', 10, 10, 10, 10)