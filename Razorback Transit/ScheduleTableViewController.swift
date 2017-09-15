//
//  ScheduleListViewController.swift
//  Razorback Transit
//
//  Created by Andrew Beers on 9/13/17.
//  Copyright © 2017 Andrew Beers. All rights reserved.
//

import UIKit

enum RouteTypes: Int {
    case Regular
    case Reduced
}

class ScheduleTableViewController: BaseTableViewController {

    @IBOutlet var ScheduleListTableView: UITableView!
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return UIStatusBarStyle.lightContent
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navBar.topItem?.title = "Schedules"
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {

        return 2
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        switch section {
        case 0:
            return Constants.regularSchedules.count
        case 1:
            return Constants.reducedSchedules.count
        default:
            return 0
        }
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        guard let cell = tableView.dequeueReusableCell(withIdentifier: "CustomTableViewCell", for: indexPath) as? CustomTableViewCell else {
            return UITableViewCell()
        }
        
        switch indexPath.section {
        case 0:
            cell.MapNameLabel.text = Constants.regularSchedules[indexPath.row].title
        case 1:
            cell.MapNameLabel.text = Constants.reducedSchedules[indexPath.row].title
        default:
            break
        }
        
        cell.accessoryType = UITableViewCellAccessoryType.disclosureIndicator
        
        return cell
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        
        switch section {
        case 0:
            return "Regular Service"
        case 1:
            return "Reduced Service"
        default:
            return ""
        }
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        
        return 50
    }

    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        
        return 30
    }
   
    // MARK: - Navigation
 
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        let destination = segue.destination as? ScheduleViewController
        
        guard let section = tableView.indexPathForSelectedRow?.section else {
            return
        }
        
        switch section {
        case 0:
            destination?.mapName = Constants.regularSchedules[(tableView.indexPathForSelectedRow?.row)!].fileName
        case 1:
            destination?.mapName = Constants.reducedSchedules[(tableView.indexPathForSelectedRow?.row)!].fileName
        default:
            destination?.mapName = ""
        }

    }
    

}
