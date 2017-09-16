//
//  RouteMapTableViewController.swift
//  Razorback Transit
//
//  Created by Andrew Beers on 9/14/17.
//  Copyright © 2017 Andrew Beers. All rights reserved.
//

import UIKit

class RouteMapTableViewController: BaseTableViewController {

    @IBOutlet var RouteMapTableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        navBar.topItem?.title = "Routes"
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
            return Constants.regularRoutes.count
        case 1:
            return Constants.reducedRoutes.count
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
            cell.MapNameLabel.text = Constants.regularRoutes[indexPath.row].title
        case 1:
            cell.MapNameLabel.text = Constants.reducedRoutes[indexPath.row].title
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
        
        let destination = segue.destination as? RouteMapWebViewController
        
        guard let section = tableView.indexPathForSelectedRow?.section else {
            return
        }
        
        switch section {
        case 0:
            destination?.mapName = Constants.regularRoutes[(tableView.indexPathForSelectedRow?.row)!].fileName
        case 1:
            destination?.mapName = Constants.reducedRoutes[(tableView.indexPathForSelectedRow?.row)!].fileName
        default:
            destination?.mapName = ""
        }
    }

}
