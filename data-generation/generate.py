import string
import requests
import random
import datetime

MIN_NAME_LENGTH = 3
MAX_NAME_LENGTH = 50
MIN_DESC_LENGTH = 10
MAX_DESC_LENGTH = 100
MIN_PRICE = 1.0
MAX_PRICE = 100.0
MIN_DURATION = 1
MAX_DURATION = 60
MIN_DATE = '01-01-2010 00:00:00'
MAX_DATE = '31-12-2020 23:59:59'

MIN_TAGS_PER_CERTIFICATE = 0
MAX_TAGS_PER_CERTIFICATE = 3

def fetch_word(min_length: int, max_length: int) -> str:
    ''' 
    Fetch a word that matches specified length boundaries 
    '''

    while True:
        # call an external API
        r = requests.get('https://random-word-api.herokuapp.com/word?number=1&swear=0')
        word = r.json()[0]

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

def generate_insert(table_name: str, cols_vals: dict) -> str:   
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

def generate_data(file_name, certificates_total, tags_total, users_total):
    '''
    Generate init data
    '''
    with open(file_name, 'a') as file:
        # certificates
        for _ in range(certificates_total):
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

            file.write(certificate_statement + '\n')

        print('Certificates successfully generated')

        # tags
        for _ in range(tags_total):
            name = fetch_word(MIN_NAME_LENGTH, MAX_NAME_LENGTH)

            tag_statement = generate_insert('tag', {
                'name': name
            })

            file.write(tag_statement + '\n')

        print('Tags generated')

        # users
        for _ in range(users_total):
            name = fetch_word(MIN_NAME_LENGTH, MAX_NAME_LENGTH)

            user_statement = generate_insert('user', {
                'name': name
            })

            file.write(user_statement + '\n')

        print('Users successfully generated')

        # link tags to 
        max_tags = MAX_TAGS_PER_CERTIFICATE if tags_total >= MAX_TAGS_PER_CERTIFICATE else tags_total
        for i in range(certificates_total):
            certificate_id = i + 1
            tags_num = random.randrange(MIN_TAGS_PER_CERTIFICATE, max_tags)

            for _ in range(tags_num):
                tag_id = random.randrange(0, tags_total) + 1
                
                certificate_tag_statement = generate_insert('certificate_tag', {
                    'id_certificate': certificate_id,
                    'id_tag': tag_id
                })

                file.write(certificate_tag_statement + '\n')

        print('Tags successfully attached to certificates')
            
# execute
generate_data('data.sql', 10, 10, 10)