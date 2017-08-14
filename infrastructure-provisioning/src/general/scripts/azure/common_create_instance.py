#!/usr/bin/python

# *****************************************************************************
#
# Copyright (c) 2016, EPAM SYSTEMS INC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# ******************************************************************************

import argparse
import json
from dlab.actions_lib import *
from dlab.meta_lib import *
import sys


parser = argparse.ArgumentParser()
parser.add_argument('--resource_group_name', type=str, default='')
parser.add_argument('--instance_name', type=str, default='')
parser.add_argument('--region', type=str, default='')
parser.add_argument('--service_base_name', type=str, default='')
parser.add_argument('--user_name', type=str, default='')
parser.add_argument('--public_key', type=str, default='')
parser.add_argument('--network_interface_resource_id', type=str, default='')
parser.add_argument('--primary_disk_size', type=str, default='')
args = parser.parse_args()


if __name__ == "__main__":
    if args.instance_name != '':
        try:
            if AzureMeta().get_instance(args.resource_group_name, args.instance_name):
                print "REQUESTED INSTANCE {} ALREADY EXISTS".format(args.instance_name)
            else:
                print "Creating instance {}".format(args.instance_name)
                AzureActions().create_instance(args.region, args.instance_size, args.service_base_name,
                                               args.instance_name, args.user_name, args.public_key,
                                               args.network_interface_resource_id, args.resource_group_name,
                                               args.primary_disk_size)
        except:
            sys.exit(1)
    else:
        sys.exit(1)